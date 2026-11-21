/*
 * Copyright (c) 2024 Explyt Ltd
 * SPDX-License-Identifier: Apache-2.0
 */

package com.explyt.spring.core.inspections.java

import com.explyt.spring.core.SpringCoreClasses
import com.explyt.spring.core.inspections.SpringBeanIncorrectAutowiringInspection
import com.explyt.spring.test.ExplytInspectionJavaTestCase
import com.explyt.spring.test.TestLibrary
import org.intellij.lang.annotations.Language

class SpringBeanIncorrectAutowiringInspectionTest : ExplytInspectionJavaTestCase() {

    override val libraries: Array<TestLibrary> = arrayOf(
        TestLibrary.springContext_6_0_7,
        TestLibrary.springTest_6_0_7,
        TestLibrary.springBootTestAutoConfigure_3_1_1
    )

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(SpringBeanIncorrectAutowiringInspection::class.java)
    }

    fun testAutowired() {
        @Language("java") val code = """
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.context.ApplicationContext;
            import org.springframework.stereotype.Component;

            @Component
            class TestInjectBean {
                @Autowired
                ApplicationContext context;
            }
        """
        myFixture.configureByText("TestInjectBean.java", code.trimIndent())
        myFixture.testHighlighting("TestInjectBean.java")
    }

    fun testAutowiredInConstructor() {
        @Language("java") val code = """
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.stereotype.Component;
            import org.springframework.stereotype.Service;

            @Service
            class MyComponent {
                @Autowired
                public <error descr="@Autowired annotation can be applied to only one constructor">MyComponent</error>(BarBean <error descr="Autowire failed. No beans of 'BarBean' found">bean</error>) {
                }

                public MyComponent() {
                }

                @Autowired
                public <error descr="@Autowired annotation can be applied to only one constructor">MyComponent</error>(FooBean foobean, BarBean <error descr="Autowire failed. No beans of 'BarBean' found">bean</error>) {
                }

                public MyComponent(FooBean foobean, BarBean bean, String str) {
                }
            }

            class BarBean {
            }

            @Component
            class FooBean {
            }

            @Service
            class <error descr="The class does not contain autowiring constructor">MyFactory</error> {
                public MyFactory(String str) {
                }
                public MyFactory(int count) {
                }
            }
        """
        myFixture.configureByText("TestAutowired.java", code.trimIndent())
        myFixture.testHighlighting("TestAutowired.java")
    }

    fun testAutowiredWithNoBean() {
        @Language("java") val code = """
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.context.annotation.Bean;
            import org.springframework.context.annotation.Configuration;
            import org.springframework.stereotype.Service;
            import java.util.Collection;
            import java.util.List;
            import java.util.Optional;
            import java.util.Set;

            @Service
            class MyComponent {
                @Autowired
                Optional<NotImplInterface> notOptional;

                @Autowired
                List<NotImplInterface> <error descr="Autowire failed. No beans of 'NotImplInterface' or 'List<NotImplInterface>' found">notList</error>;

                @Autowired
                Collection<NotImplInterface> <error descr="Autowire failed. No beans of 'NotImplInterface' or 'Collection<NotImplInterface>' found">notCollection</error>;

                @Autowired
                Set<NotImplInterface> <error descr="Autowire failed. No beans of 'NotImplInterface' or 'Set<NotImplInterface>' found">notSet</error>;

                @Autowired
                BarBean[] <error descr="Autowire failed. No beans of 'BarBean' or 'BarBean[]' found">noArray</error>;
                OtherBean other;

                @Autowired
                void setOther(OtherBean <error descr="Autowire failed. No beans of 'OtherBean' found">value</error>) {
                    other = value;
                }

                @Autowired
                MyBean <error descr="Autowire failed. No beans of 'MyBean' found">beanA</error>;
            }

            class MyBean {
            }

            interface NotImplInterface {
            }

            class OtherBean {
                @Bean
                public MyBean myBean() {
                    return new MyBean();
                }
            }

            class BarBean {
            }

            class FooBean {
            }

            class FooBeanClass {
                @Autowired
                FooBean <error descr="Must be defined in valid Spring bean (@Component|@Service|...)">bean</error>;
            }

            @Configuration
            class ConfigurationBean {
                @Bean
                ClassBeanExist createBean() {
                    return new ClassBeanExist();
                }
            }

            class ClassNotBeanExist {
                @Autowired
                InjectBean <error descr="Must be defined in valid Spring bean (@Component|@Service|...)">injectBean</error>;
            }

            class ClassBeanExist {
                @Autowired
                InjectBean injectBean;
            }

            interface InjectBean {
            }

            @Service
            class IBComponent implements InjectBean {
            }
        """
        myFixture.configureByText("TestAutowired.java", code.trimIndent())
        myFixture.testHighlighting("TestAutowired.java")
    }

    fun testAutowiredWithManyBeans() {
        @Language("java") val code = """
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.context.annotation.Bean;
            import org.springframework.context.annotation.Configuration;
            import org.springframework.context.annotation.Primary;
            import org.springframework.stereotype.Component;

            @Configuration
            class MyComponent {
                @Autowired
                FooInterface <error descr="Autowire failed. There is more than one bean of 'FooInterface' Beans: [fooBean] {@link FooBean} [nameComponent] {@link ThreeBean} [otherBean] {@link OtherBean}">one</error>;

                @Autowired
                FooInterface nameComponent;

                @Autowired
                BarInterface primer;
            }

            @Component
            class FooBeanComponent {
                @Autowired FooBean1 fooBean1;
                @Autowired FooBeanInterface fooBeanInterface;
                @Autowired FooBean2 fooBean2;
            }

            interface FooInterface {
            }

            @Component
            class FooBean implements FooInterface {
            }

            @Component
            class OtherBean implements FooInterface {
            }

            @Component("nameComponent")
            class ThreeBean implements FooInterface {
            }

            interface BarInterface {
            }

            @Component
            class BarImpl implements BarInterface {
            }

            @Component
            @Primary
            class PrimerBarImpl implements BarInterface {
            }

            @Configuration
            class TestConfiguration {
                @Bean FooBean1 createBean1() {
                    return new FooBean1();
                }

                @Primary
                @Bean FooBean1 fooBeanPrimary() {
                    return new FooBean1();
                }

                @Bean FooBean2 createBean2() {
                    return new FooBean2();
                }
            }

            interface FooBeanInterface {
            }

            class FooBean1 implements FooBeanInterface {
            }

            class FooBean2 implements FooBeanInterface {
            }
        """
        myFixture.configureByText("TestAutowired.java", code.trimIndent())
        myFixture.testHighlighting("TestAutowired.java")
    }

    fun testAutowiredWithQualifier() {
        @Language("java") val code = """
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.beans.factory.annotation.Qualifier;
            import org.springframework.context.annotation.Configuration;
            import org.springframework.stereotype.Component;

            @Configuration
            class MyComponent {
                @Qualifier("fooBean")
                @Autowired
                FooInterface testQualifier1;

                @Qualifier("not")
                @Autowired
                FooInterface <error descr="Autowire failed. No beans of 'FooInterface' found">testQualifier2</error>;

                @Qualifier("nameQualifier")
                @Autowired
                FooInterface testQualifier3;
            }

            interface FooInterface {
            }

            @Component
            class FooBean implements FooInterface {
            }

            @Component
            class OtherBean implements FooInterface {
            }

            @Component
            @Qualifier("nameQualifier")
            class AnotherBean implements FooInterface {
            }
        """
        myFixture.configureByText("TestAutowired.java", code.trimIndent())
        myFixture.testHighlighting("TestAutowired.java")
    }

    fun testResourceLoader() {
        @Language("java") val code = """
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.context.ApplicationContext;
            import org.springframework.core.io.ResourceLoader;
            import org.springframework.context.support.AbstractApplicationContext;

            @${SpringCoreClasses.COMPONENT}
            public class DemoApplication {
                @Autowired
                ResourceLoader resourceLoader;
                @Autowired
                ApplicationContext context;
                @Autowired
                AbstractApplicationContext abstractContext;
            }
        """
        myFixture.configureByText("DemoApplication.java", code.trimIndent())
        myFixture.testHighlighting("DemoApplication.java")
    }

    fun testInheritedAutowiredMethodDoesNotCreateProblemInForeignFile() {
        myFixture.addClass(
            """
            public class MissingBean {
            }
            """.trimIndent()
        )
        myFixture.addClass(
            """
            import org.springframework.beans.factory.annotation.Autowired;

            public class ParentService {
                @Autowired
                public void setMissingBean(MissingBean missingBean) {
                }
            }
            """.trimIndent()
        )

        @Language("java") val code = """
            import org.springframework.stereotype.Component;

            @Component
            public class <error descr="Autowire failed. No beans of 'MissingBean' found">ChildService</error> extends ParentService {
            }
        """
        myFixture.configureByText("ChildService.java", code.trimIndent())
        myFixture.testHighlighting("ChildService.java")
    }

    fun testAutowiredInContextConfigurationTestClass() {
        @Language("java") val code = """
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.test.context.ContextConfiguration;

            @${SpringCoreClasses.COMPONENT}
            class SomeService {
            }

            @ContextConfiguration(classes = SomeService.class)
            public class SomeServiceTest {
                @Autowired
                SomeService service;
            }
        """
        myFixture.configureByText("SomeServiceTest.java", code.trimIndent())
        myFixture.testHighlighting("SomeServiceTest.java")
    }
}
