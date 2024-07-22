package com.ancevt.ini

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IniTest {

    companion object {
        private val TEST_CONTENT: String = IniTest::class.java
            .classLoader
            .getResourceAsStream("test.ini")
            ?.bufferedReader(Charsets.UTF_8)
            ?.readText()!!
    }

    @Test
    fun testRuntimeBuildIni() {
        val ini = Ini()
        ini.topLevelSection.apply {
            put("testKey", "testValue")
            createEmptyLine();
        }

        ini.createSection("Section 1").apply {
            put("key1", "value1")
            put("key2", "value2")
            createEmptyLine()
            createComment(" This is a comment")
            put("key3", "value3")
            put("key4", "value4")
        }

        ini.createSection("Section 2").apply {
            put("key1", "value1")
            put("key2", "value2")
            createEmptyLine()
            createComment(" This is a comment")
            put("checkMeKey", "checkMeValue")
            put("key3", "value3")
            put("key4", "value4")
        }

        assertEquals("testValue", ini.topLevelSection["testKey"])
        assertEquals("checkMeValue", ini["Section 2"]?.get("checkMeKey"))
    }

    @Test
    fun testRemoveSection() {
        val ini = Ini(TEST_CONTENT)

        ini.removeSection("Section2")

        assertEquals(1, ini.numSections)
        assertFalse(ini.containsSection("Section2"))
    }

    @Test
    fun testRemoveValue() {
        val ini = Ini(TEST_CONTENT)

        ini["Section1"]?.remove("id")

        assertEquals(1, ini["Section1"]?.numRecords)
        assertFalse(ini["Section1"]?.containsKey("id")!!)
    }

    @Test
    fun testRecordsAsMap() {
        val ini = Ini(TEST_CONTENT)

        val map = ini.topLevelSection.records;

        assertEquals(2, map.size)
        assertEquals("this is global value", map["global1"])
    }

    @Test
    fun testGetTyped() {
        val ini = Ini(TEST_CONTENT)

        val l = ini["Section2"]?.getLong("long")
        val i = ini["Section2"]?.getInt("int")
        val f = ini["Section2"]?.getFloat("float")
        val d = ini["Section2"]?.getDouble("double")
        val b = ini["Section2"]?.getBoolean("boolean")

        assertEquals(120, i)
        assertEquals(120L, l)
        assertEquals(120.1f, f)
        assertEquals(120.1, d)
        assertTrue(b!!)
    }

    @Test
    fun testFails() {
        assertFails { Ini("123") }
        assertFails { Ini("=") }
        assertFails { Ini(" = = = ") }
        assertFails { Ini("[UnclosedSection") }
        assertFails { Ini("[UnclosedSection]]") }
    }

    @Test
    fun testDuplicateSections() {
        val ini = Ini(
            """
            [Section1]
            A=1
            B=2
            [Section2]
            A=1
            B=2
            [Section1]
            C=1
        """.trimIndent()
        )

        println(ini)
    }

    @Test
    fun testIterateOverAll() {
        val ini = Ini(TEST_CONTENT)

        println(ini)
    }
}