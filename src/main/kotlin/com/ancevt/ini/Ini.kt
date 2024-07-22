package com.ancevt.ini

import java.io.InputStream

open class Ini() {

    val topLevelSection: Section = Section("__top_level_section__")
    val sections = mutableListOf<Section>()

    val sectionsIncludingTopLevel: Collection<Section> get() = listOf(topLevelSection) + sections

    val numSections: Int get() = sections.size

    constructor(inputStream: InputStream) : this() {
        val content = inputStream.bufferedReader(Charsets.UTF_8).readText()
        parse(content)
    }

    constructor(source: String) : this() {
        parse(source)
    }

    private fun parse(content: String) {
        var section = topLevelSection

        content.lines().forEachIndexed { index, it ->
            val line = it.trimStart()
            val lineNumber = index + 1

            if (line.startsWith("[") && line.trim().endsWith("]")) {
                val sectionName = line.substring(1, line.length - 1).trim()

                if (sectionName.contains("[") || sectionName.contains("]"))
                    throw IniException("Invalid section definition at line $lineNumber: $line")

                section = getOrCreateSection(sectionName)
            } else if (line.trim().isBlank()) {
                section.entries.add(Entry(null, null, false, true))
            } else if (line.startsWith(";") || line.startsWith("#")) {
                val value = line.substring(1).trim()
                section.entries.add(Entry(null, value, true, false, line[0]))
            } else if (line.contains("=")) {
                val split = line.split("=", limit = 2)
                val key = split[0].trim()
                val value = split[1].trimStart()

                if (key.isBlank()) throw IniException("Blank key at line $lineNumber: $line")

                section.entries.add(Entry(key, value, false, false))
            } else {
                throw IniException("Parse error at line $lineNumber: $line")
            }
        }
    }

    private fun getOrCreateSection(sectionName: String): Section {
        return sections.find { it.name == sectionName } ?: Section(sectionName).also { sections.add(it) }
    }

    operator fun get(sectionName: String): Section? {
        return sections.find { it.name == sectionName }
    }

    fun createSection(sectionName: String): Section {
        if (containsSection(sectionName)) {
            throw IllegalStateException("Section already exists")
        }

        val section = Section(sectionName)
        sections.add(section)
        return section
    }

    fun removeSection(sectionName: String) {
        sections.removeIf { it.name == sectionName }
    }

    fun containsSection(sectionName: String): Boolean {
        return sections.any { it.name == sectionName }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        appendSectionToStringBuilder(topLevelSection, sb)
        sections.forEach { appendSectionToStringBuilder(it, sb) }
        return sb.toString()
    }

    private fun appendSectionToStringBuilder(section: Section, sb: StringBuilder) {
        if (section != topLevelSection) sb.appendLine("[${section.name}]")

        section.entries.forEach { entry ->
            if (entry.isComment) {
                sb.appendLine(entry.toString())
            } else if (entry.isEmpty) {
                sb.appendLine()
            } else {
                sb.appendLine("${entry.key}=${entry.value}")
            }
        }
    }

    class Section(val name: String) {
        val entries = mutableListOf<Entry>()

        val numRecords: Int
            get() {
                return entries.count { it.key != null }
            }

        val records: Map<String, String>
            get() = entries
                .filter { it.key != null && it.value != null }
                .associate { it.key!! to it.value!! }

        operator fun get(key: String): String? {
            return entries.find { it.key == key }?.value
        }

        fun getByte(key: String, default: Byte = 0): Byte {
            return this[key]?.toByteOrNull() ?: default
        }

        fun getShort(key: String, default: Short = 0): Short {
            return this[key]?.toShortOrNull() ?: default
        }

        fun getChar(key: String, default: Char = '?'): Char {
            return this[key]?.toCharArray()?.get(0) ?: default
        }

        fun getInt(key: String, default: Int = 0): Int {
            return this[key]?.toIntOrNull() ?: default
        }

        fun getDouble(key: String, default: Double = 0.0): Double {
            return this[key]?.toDoubleOrNull() ?: default
        }

        fun getFloat(key: String, default: Float = 0.0f): Float {
            return this[key]?.toFloatOrNull() ?: default
        }

        fun getLong(key: String, default: Long = 0L): Long {
            return this[key]?.toLongOrNull() ?: default
        }

        fun getBoolean(key: String, default: Boolean = false): Boolean {
            return this[key]?.toBoolean() ?: default
        }

        fun put(key: String, value: String): Section {
            if (containsKey(key)) {
                entries.find { it.key == key }?.value = value
            } else {
                entries.add(Entry(key, value, isComment = false, isEmpty = false))
            }
            return this
        }

        fun createEmptyLine(): Section {
            entries.add(Entry(null, null, isComment = false, isEmpty = true))
            return this
        }

        fun createComment(comment: String): Section {
            entries.add(Entry(null, comment, isComment = true, isEmpty = false))
            return this
        }

        fun remove(key: String) {
            entries.removeIf { it.key == key }
        }

        fun containsKey(key: String): Boolean {
            return entries.any { it.key == key }
        }

        override fun toString(): String {
            return "[$name]"
        }
    }

    data class Entry(
        val key: String?,
        var value: String?,
        val isComment: Boolean = false,
        val isEmpty: Boolean = false,
        val commentChar: Char = ';'
    ) {

        override fun toString(): String {
            if (isEmpty) return ""

            return if (isComment) "$commentChar$value" else "$key=$value"
        }
    }

    class IniException(message: String) : Exception(message)
}
