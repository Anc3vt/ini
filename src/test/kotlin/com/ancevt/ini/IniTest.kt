package com.ancevt.ini

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

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
        ini.defaultSection
            .put("testKey", "testValue")
            .createEmptyLine();

        ini.createSection("Section 1")
            .put("key1", "value1")
            .put("key2", "value2")
            .createEmptyLine()
            .createComment(" This is a comment")
            .put("key3", "value3")
            .put("key4", "value4")

        ini.createSection("Section 2")
            .put("key1", "value1")
            .put("key2", "value2")
            .createEmptyLine()
            .createComment(" This is a comment")
            .put("checkMeKey", "checkMeValue")
            .put("key3", "value3")
            .put("key4", "value4")

        assertEquals("testValue", ini.defaultSection["testKey"])
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
}