/*
 * Copyright (c) 2024 Explyt Ltd
 * SPDX-License-Identifier: Apache-2.0
 */

package com.explyt.spring.core.inspections.kotlin

import com.explyt.spring.core.SpringCoreClasses
import com.explyt.spring.core.inspections.SpringBeanIncorrectAutowiringInspection
import com.explyt.spring.test.ExplytInspectionKotlinTestCase
import com.explyt.spring.test.TestLibrary
import org.intellij.lang.annotations.Language

class SpringBeanIncorrectAutowiringInspectionTest : ExplytInspectionKotlinTestCase() {

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
        @Language("kotlin") val code = """
            import org.springframework.beans.factory.annotation.Autowired
            import org.springframework.context.ApplicationContext
            import org.springframework.stereotype.Component

            @Component
            open class TestInjectBean {
                @Autowired
                var context: ApplicationContext? = null
            }
        """
        myFixture.configureByText("TestInjectBean.kt", code.trimIndent())
        myFixture.testHighlighting("TestInjectBean.kt")
    }

    fun testAutowiredInConstructor() {
        @Language("kotlin") val code = """
            import org.springframework.beans.factory.annotation.Autowired
            import org.springframework.stereotype.Component
            import org.springframework.stereotype.Service

            @Service
            open class MyComponent {
                @Autowired
                <error descr="@Autowired annotation can be applied to only one constructor">constructor</error>(<error descr="Autowire failed. No beans of 'BarBean' found">bean</error>: BarBean?)

                @Autowired
                <error descr="@Autowired annotation can be applied to only one constructor"><error descr="Autowire failed. Method without parameters">constructor</error></error>()

                @Autowired
                <error descr="@Autowired annotation can be applied to only one constructor">constructor</error>(foobean: FooBean?, <error descr="Autowire failed. No beans of 'BarBean' found">bean</error>: BarBean?)

                @Autowired(required = false)
                <error descr="@Autowired annotation can be applied to only one constructor">constructor</error>(foobean: FooBean?, bean: BarBean?, str: String?)
            }

            class BarBean

            @Component
            open class FooBean

            @Service
            open class <error descr="The class does not contain autowiring constructor">MyFactory</error> {
                constructor(str: String?)
                constructor(count: Int)
            }
        """
        myFixture.configureByText("TestAutowired.kt", code.trimIndent())
        myFixture.testHighlighting("TestAutowired.kt")
    }

    fun testAutowiredWithNoBean() {
        @Language("kotlin") val code = """
            import org.springframework.beans.factory.annotation.Autowired
            import org.springframework.context.annotation.Bean
            import org.springframework.context.annotation.Configuration
            import org.springframework.stereotype.Service
            import java.util.*

            @Service
            open class MyComponent {
                @Autowired
                var notOptional: Optional<NotImplInterface>? = null

                @Autowired
                var <error descr="Autowire failed. No beans of 'List' or 'List<? extends NotImplInterface>' found">notList</error>: List<NotImplInterface>? = null

                @Autowired
                var <error descr="Autowire failed. No beans of 'Collection' or 'Collection<? extends NotImplInterface>' found">notCollection</error>: Collection<NotImplInterface>? = null

                @Autowired
                var <error descr="Autowire failed. No beans of 'Set' or 'Set<? extends NotImplInterface>' found">notSet</error>: Set<NotImplInterface>? = null

                @Autowired
                var <error descr="Autowire failed. No beans of 'BarBean' or 'BarBean[]' found">noArray</error>: Array<BarBean> = emptyArray()
                var other: OtherBean? = null

                @Autowired
                fun setOther(<error descr="Autowire failed. No beans of 'OtherBean' found">value</error>: OtherBean?) {
                    other = value
                }

                @Autowired
                var <error descr="Autowire failed. No beans of 'MyBean' found">beanA</error>: MyBean? = null
            }

            open class MyBean
            interface NotImplInterface

            open class OtherBean {
                @Bean
                fun myBean(): MyBean = MyBean()
            }

            class BarBean
            open class FooBean

            open class FooBeanClass {
                @Autowired
                var <error descr="Must be defined in valid Spring bean (@Component|@Service|...)">bean</error>: FooBean? = null
            }

            @Configuration
            open class ConfigurationBean {
                @Bean
                open fun createBean(): ClassBeanExist = ClassBeanExist()
            }

            open class ClassNotBeanExist {
                @Autowired
                var <error descr="Must be defined in valid Spring bean (@Component|@Service|...)">injectBean</error>: InjectBean? = null
            }

            open class ClassBeanExist {
                @Autowired
                var injectBean: InjectBean? = null
            }

            interface InjectBean

            @Service
            open class IBComponent : InjectBean
        """
        myFixture.configureByText("TestAutowired.kt", code.trimIndent())
        myFixture.testHighlighting("TestAutowired.kt")
    }

    fun testAutowiredWithManyBeans() {
        @Language("kotlin") val code = """
            import org.springframework.beans.factory.annotation.Autowired
            import org.springframework.context.annotation.Bean
            import org.springframework.context.annotation.Configuration
            import org.springframework.context.annotation.Primary
            import org.springframework.stereotype.Component

            @Configuration
            open class MyComponent {
                @Autowired
                var <error descr="Autowire failed. There is more than one bean of 'FooInterface' Beans: [fooBean] {@link FooBean} [nameComponent] {@link ThreeBean} [otherBean] {@link OtherBean}">one</error>: FooInterface? = null

                @Autowired
                var nameComponent: FooInterface? = null

                @Autowired
                var primer: BarInterface? = null
            }

            @Component
            open class FooBeanComponent {
                @Autowired
                var fooBean1: FooBean1? = null
                @Autowired
                var fooBeanInterface: FooBeanInterface? = null
                @Autowired
                var fooBean2: FooBean2? = null
            }

            interface FooInterface
            @Component
            open class FooBean : FooInterface
            @Component
            open class OtherBean : FooInterface
            @Component("nameComponent")
            class ThreeBean : FooInterface

            interface BarInterface
            @Component
            class BarImpl : BarInterface
            @Component
            @Primary
            class PrimerBarImpl : BarInterface

            @Configuration
            open class TestConfiguration {
                @Bean
                open fun createBean1(): FooBean1 = FooBean1()

                @Primary
                @Bean
                open fun fooBeanPrimary(): FooBean1 = FooBean1()

                @Bean
                open fun createBean2(): FooBean2 = FooBean2()
            }

            interface FooBeanInterface
            class FooBean1 : FooBeanInterface
            class FooBean2 : FooBeanInterface
        """
        myFixture.configureByText("TestAutowired.kt", code.trimIndent())
        myFixture.testHighlighting("TestAutowired.kt")
    }

    fun testAutowiredWithQualifier() {
        @Language("kotlin") val code = """
            import org.springframework.beans.factory.annotation.Autowired
            import org.springframework.beans.factory.annotation.Qualifier
            import org.springframework.context.annotation.Configuration
            import org.springframework.stereotype.Component

            @Configuration
            open class MyComponent {
                @Qualifier("fooBean")
                @Autowired
                var testQualifier1: FooInterface? = null

                @Qualifier("not")
                @Autowired
                var <error descr="Autowire failed. No beans of 'FooInterface' found">testQualifier2</error>: FooInterface? = null

                @Qualifier("nameQualifier")
                @Autowired
                var testQualifier3: FooInterface? = null
            }

            interface FooInterface
            @Component
            open class FooBean : FooInterface
            @Component
            open class OtherBean : FooInterface
            @Component
            @Qualifier("nameQualifier")
            open class AnotherBean : FooInterface
        """
        myFixture.configureByText("TestAutowired.kt", code.trimIndent())
        myFixture.testHighlighting("TestAutowired.kt")
    }

    fun testResourceLoader() {
        @Language("kotlin") val code = """
            import org.springframework.beans.factory.annotation.Autowired
            import org.springframework.context.ApplicationContext
            import org.springframework.core.io.ResourceLoader
            import org.springframework.context.support.AbstractApplicationContext

            @${SpringCoreClasses.COMPONENT}
            class DemoApplication {
                @Autowired var resourceLoader: ResourceLoader? = null
                @Autowired var context: ApplicationContext? = null
                @Autowired var abstractContext: AbstractApplicationContext? = null
            }
        """
        myFixture.configureByText("DemoApplication.kt", code.trimIndent())
        myFixture.testHighlighting("DemoApplication.kt")
    }

    fun testAutowiredInContextConfigurationTestClass() {
        @Language("kotlin") val code = """
            import org.springframework.beans.factory.annotation.Autowired
            import org.springframework.test.context.ContextConfiguration

            @${SpringCoreClasses.COMPONENT}
            class SomeService

            @ContextConfiguration(classes = [SomeService::class])
            class SomeServiceTest {
                @Autowired
                lateinit var service: SomeService
            }
        """
        myFixture.configureByText("SomeServiceTest.kt", code.trimIndent())
        myFixture.testHighlighting("SomeServiceTest.kt")
    }
}
