/*
 * Copyright (c) 2024 Explyt Ltd
 * SPDX-License-Identifier: Apache-2.0
 */

package com.explyt.spring.core.providers.kotlin

import com.explyt.spring.core.SpringCoreClasses
import com.explyt.spring.core.SpringIcons
import com.explyt.spring.test.ExplytKotlinLightTestCase
import com.explyt.spring.test.TestLibrary
import com.explyt.spring.test.util.SpringGutterTestUtil
import com.explyt.spring.test.util.SpringGutterTestUtil.getAllBeanGuttersByIcon
import junit.framework.TestCase
import org.intellij.lang.annotations.Language

private const val TEST_DATA_PATH = "providers/linemarkers/beans"

class SpringBeanLineMarkerProviderSimpleTest : ExplytKotlinLightTestCase() {
    override fun getTestDataPath(): String = super.getTestDataPath() + TEST_DATA_PATH

    override val libraries: Array<TestLibrary> = arrayOf(
        TestLibrary.springContext_6_0_7,
        TestLibrary.jakarta_annotation_2_1_1
    )

    fun testLineMarkerOneBeanOneDependency_toAutowired() {
        val vf = myFixture.copyFileToProject(
            "one_bean_one_dependency/TestConfiguration.kt"
        )
        myFixture.configureFromExistingVirtualFile(vf)
        myFixture.doHighlighting()

        val icons = setOf(SpringIcons.SpringBean, SpringIcons.springBeanInactive)
        val allBeanGutters = getAllBeanGuttersByIcon(myFixture, icons)
        val gutterTargetString = allBeanGutters.asSequence()
            .map { SpringGutterTestUtil.getGutterTargetsStrings(it) }
            .filter { it.isNotEmpty() }
            .toList()

        assertTrue(allBeanGutters.isNotEmpty())
        TestCase.assertEquals(1, gutterTargetString.flatMap { gutter ->
            gutter.filter { it.contains("fooAOneBeanOneDependency", true) }
        }.size)
        TestCase.assertEquals(0, gutterTargetString.flatMap { gutter ->
            gutter.filter { it.contains("fooBOneBeanOneDependency", true) }
        }.size)
        TestCase.assertEquals(1, gutterTargetString.flatMap { gutter ->
            gutter.filter { it.contains("fooCOneBeanOneDependency", true) }
        }.size)
    }

    fun testLineMarkerOneBeanOneDependency_toBean() {
        val vf = myFixture.copyFileToProject(
            "one_bean_one_dependency/TestConfiguration.kt"
        )
        myFixture.configureFromExistingVirtualFile(vf)
        myFixture.doHighlighting()

        val allBeanGutters = myFixture.findAllGutters()
            .filter { it.icon == SpringIcons.SpringBeanDependencies }
        val gutterTargetString = allBeanGutters.asSequence()
            .map { SpringGutterTestUtil.getGutterTargetsStrings(it) }
            .filter { it.isNotEmpty() }
            .toList()

        assertTrue(allBeanGutters.isNotEmpty())
        TestCase.assertEquals(2, gutterTargetString.flatMap { gutter ->
            gutter.filter { it.contains("fooAOneBeanOneDependency", true) }
        }.size)
        TestCase.assertEquals(0, gutterTargetString.flatMap { gutter ->
            gutter.filter { it.contains("fooBOneBeanOneDependency", true) }
        }.size)
        TestCase.assertEquals(1, gutterTargetString.flatMap { gutter ->
            gutter.filter { it.contains("fooCOneBeanOneDependency", true) }
        }.size)
    }

    fun testLineMarkerOneBeanTwoDependency_toAutowired() {
        val vf = myFixture.copyFileToProject(
            "one_bean_two_dependency/TestConfiguration.kt"
        )
        myFixture.configureFromExistingVirtualFile(vf)
        myFixture.doHighlighting()

        val allBeanGutters = myFixture.findAllGutters()
            .filter { it.icon == SpringIcons.SpringBean }
        val gutterTargetString = allBeanGutters.asSequence()
            .map { SpringGutterTestUtil.getGutterTargetsStrings(it) }
            .filter { it.isNotEmpty() }
            .toList()

        assertTrue(allBeanGutters.isNotEmpty())
        for (gutter in gutterTargetString) {
            TestCase.assertEquals(2, gutter.filter { it.contains("foo", true) }.size)
        }
    }

    fun testLineMarkerOneBeanTwoDependency_toBean() {
        val vf = myFixture.copyFileToProject(
            "one_bean_two_dependency/TestConfiguration.kt"
        )
        myFixture.configureFromExistingVirtualFile(vf)
        myFixture.doHighlighting()

        val allBeanGutters = myFixture.findAllGutters()
            .filter { it.icon == SpringIcons.SpringBeanDependencies }
        val gutterTargetString = allBeanGutters.asSequence()
            .map { SpringGutterTestUtil.getGutterTargetsStrings(it) }
            .filter { it.isNotEmpty() }
            .toList()

        assertTrue(allBeanGutters.isNotEmpty())

        for (gutter in gutterTargetString) {
            TestCase.assertEquals(1, gutter.filter { it.contains("foo", true) }.size)
        }
    }

    fun testLineMarker_toAutowired() {
        val vf = myFixture.copyFileToProject(
            "autowired/FooFormatter.kt"
        )
        myFixture.configureFromExistingVirtualFile(vf)
        myFixture.doHighlighting()

        val allBeanGutters = myFixture.findAllGutters()
            .filter { it.icon == SpringIcons.SpringBean }
        val gutterTargetString = allBeanGutters.asSequence()
            .map { SpringGutterTestUtil.getGutterTargetsStrings(it) }
            .filter { it.isNotEmpty() }
            .toList()

        assertTrue(allBeanGutters.isNotEmpty())
        TestCase.assertEquals(3, gutterTargetString.flatMap { gutter ->
            gutter.filter { it.contains("fooFormatter", true) }
        }.size)
    }

    fun testLineMarker_toBean() {
        val vf = myFixture.copyFileToProject(
            "autowired/FooFormatter.kt"
        )
        myFixture.configureFromExistingVirtualFile(vf)
        myFixture.doHighlighting()

        val allBeanGutters = myFixture.findAllGutters()
            .filter { it.icon == SpringIcons.SpringBeanDependencies }
        val gutterTargetString = allBeanGutters.asSequence()
            .map { SpringGutterTestUtil.getGutterTargetsStrings(it) }
            .filter { it.isNotEmpty() }
            .toList()

        assertTrue(allBeanGutters.isNotEmpty())
        TestCase.assertEquals(3, gutterTargetString.flatMap { gutter ->
            gutter.filter { it.contains("fooFormatter", true) }
        }.size)
    }

    fun testLineMarkerByBeanName_toAutowired() {
        val vf = myFixture.copyFileToProject(
            "method_navigates/TestConfiguration.kt"
        )
        myFixture.configureFromExistingVirtualFile(vf)
        myFixture.doHighlighting()

        val icons = setOf(SpringIcons.SpringBean, SpringIcons.springBeanInactive)
        val allBeanGutters = getAllBeanGuttersByIcon(myFixture, icons)
        val gutterTargetString = SpringGutterTestUtil.getGutterTargetString(allBeanGutters)

        TestCase.assertEquals(
            2,
            gutterTargetString
                .filter { it.size == 1 }
                .flatMap { gutter ->
                    gutter.filter { it.contains("namedBeanFoo", true) || it.contains("foo", true) }
                }.size
        )
    }

    fun testLineMarkerByBeanName_toBean() {
        val vf = myFixture.copyFileToProject(
            "method_navigates/TestConfiguration.kt"
        )
        myFixture.configureFromExistingVirtualFile(vf)
        myFixture.doHighlighting()

        val allBeanGutters = getAllBeanGuttersByIcon(myFixture, SpringIcons.SpringBeanDependencies)
        val gutterTargetString = SpringGutterTestUtil.getGutterTargetString(allBeanGutters)

        TestCase.assertEquals(5, gutterTargetString.flatMap { gutter ->
            gutter.filter {
                it.contains("namedBeanFoo", true)
                        || it.contains("foo", true)
            }
        }.size)
    }

    fun testLineMarkerResource_toAutowired() {
        myFixture.configureByText(
            "FooComponent.kt",
            """
                @org.springframework.stereotype.Component
                internal class E {}

                @org.springframework.stereotype.Component
                internal class Foo {
                    @jakarta.annotation.Resource
                    private lateinit var e: E
                }
            """.trimIndent()
        )
        myFixture.doHighlighting()

        val allBeanGutters = getAllBeanGuttersByIcon(myFixture, SpringIcons.SpringBean)
        val gutterTargetString = SpringGutterTestUtil.getGutterTargetString(allBeanGutters)

        TestCase.assertEquals(
            1, gutterTargetString.flatMap { gutter -> gutter.filter { it == "e" } }.size
        )
    }

    fun testLineMarkerResource_toBean() {
        myFixture.configureByText(
            "FooComponent.kt",
            """
                @org.springframework.stereotype.Component
                internal class E {}

                @org.springframework.stereotype.Component
                internal class Foo {
                    @jakarta.annotation.Resource
                    private lateinit var e: E
                }
            """.trimIndent()
        )
        myFixture.doHighlighting()

        val allBeanGutters = getAllBeanGuttersByIcon(myFixture, SpringIcons.SpringBeanDependencies)
        val gutterTargetString = SpringGutterTestUtil.getGutterTargetString(allBeanGutters)

        TestCase.assertEquals(
            1, gutterTargetString.flatMap { gutter -> gutter.filter { it == "E" } }.size
        )
    }

    fun testLineMarker_BeanDeclaration() {
        myFixture.configureByText(
            "FooComponent.kt",
            """
                class Foo

                @${SpringCoreClasses.CONFIGURATION}
                open class FooComponent {
                
                    @${SpringCoreClasses.BEAN}
                    fun foo():Foo {
                        return Foo()
                    }                                    
                }

            """.trimIndent()
        )
        myFixture.doHighlighting()

        val allBeanGutters = getAllBeanGuttersByIcon(myFixture, SpringIcons.SpringBean)
        val gutterTargetString = SpringGutterTestUtil.getGutterTargetString(allBeanGutters)

        assertTrue(gutterTargetString.flatten().all { it == "foo()" })
    }

    fun testNoLineMarkerOnConstructorParam() {
        val person = """                
                class Person(val name:String)
            """.trimIndent()

        myFixture.configureByText("Person.kt", person)
        myFixture.doHighlighting()

        val allBeanGutters = myFixture.findAllGutters()
            .filter { it.icon == SpringIcons.SpringBeanDependencies }
        assertTrue(allBeanGutters.isEmpty())
    }

    fun testBeanDependenciesLineMarker_AutowiredInEmptyClass() {
        @Language("kotlin") val code = """  
                class FooComponent {
                    @${SpringCoreClasses.AUTOWIRED}
                    lateinit var stringBean: String                                     
                }
            """
        myFixture.configureByText("FooComponent.kt", code.trimIndent())
        myFixture.doHighlighting()

        val allBeanGutters = getAllBeanGuttersByIcon(myFixture, SpringIcons.SpringBeanDependencies)
        assertFalse(allBeanGutters.isEmpty())
    }
}