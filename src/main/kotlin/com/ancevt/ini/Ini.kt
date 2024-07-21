package com.ancevt.ini

import java.io.InputStream

class Ini() {

    val defaultSection: Section = Section("__default_section__")
    private val sections = mutableListOf<Section>()

    val numSections: Int
        get() {
            return sections.size
        }

    constructor(inputStream: InputStream) : this() {
        val content = inputStream.bufferedReader(Charsets.UTF_8).readText()
        parse(content)
    }

    constructor(source: String) : this() {
        parse(source)
    }

    private fun parse(content: String) {
        var section = defaultSection

        content.lines().forEach {
            val line = it.trimStart()

            if (line.startsWith("[")) {
                val sectionName = line.substring(1, line.length - 1).trim()

                section = Section(sectionName)
                sections.add(section)

            } else if (line.trim().isBlank()) {
                section.entries.add(Entry(null, null, false, true))

            } else if (line.startsWith(";") || line.startsWith("#")) {
                val value = line.substring(1)
                section.entries.add(Entry(null, value, true, false))

            } else if (line.contains("=")) {
                val split = line.split("=", limit = 2)
                val key = split[0].trim()
                val value = split[1].trimStart()
                section.entries.add(Entry(key, value, false, false))

            }
        }
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
        appendSectionToStringBuilder(defaultSection, sb)
        sections.forEach { appendSectionToStringBuilder(it, sb) }
        return sb.toString()
    }

    private fun appendSectionToStringBuilder(section: Section, sb: StringBuilder) {
        if (section != defaultSection) sb.appendLine("[${section.name}]")

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

        operator fun get(key: String): String? {
            return entries.find { it.key == key }?.value
        }

        fun put(key: String, value: String):Section {
            if (containsKey(key)) {
                entries.find { it.key == key }?.value = value
            } else {
                entries.add(Entry(key, value, isComment = false, isEmpty = false))
            }
            return this
        }

        fun createEmptyLine():Section {
            entries.add(Entry(null, null, isComment = false, isEmpty = true))
            return this
        }

        fun createComment(comment:String):Section {
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
        val isComment: Boolean,
        val isEmpty: Boolean
    ) {

        override fun toString(): String {
            if (isEmpty) return ""

            return if (isComment) ";$value" else "$key = $value"
        }
    }
}
