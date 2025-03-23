package com.wutsi.koki.file.server.service

import com.wutsi.koki.file.server.dao.LabelRepository
import com.wutsi.koki.file.server.domain.LabelEntity
import jakarta.transaction.Transactional
import org.apache.commons.text.WordUtils
import org.springframework.stereotype.Service
import java.text.Normalizer
import java.util.Date

@Service
class LabelService(
    private val dao: LabelRepository
) {
    companion object {
        private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        private val FILTER1 = "\\s+|-+|\\p{Punct}".toRegex() // space, punctuation
        private val FILTER2 = "[-*]{2,}".toRegex() // Multiple separators
        private val SEPARATOR = "-"
    }

    fun find(names: List<String>): List<LabelEntity> {
        if (names.isEmpty()) {
            return emptyList()
        }
        return dao.findByNameIn(names.map { toName(it) })
    }

    @Transactional
    fun findOrCreate(names: List<String>): List<LabelEntity> {
        if (names.isEmpty()) {
            return mutableListOf()
        }

        val tags = find(names)
        if (tags.size == names.size) {
            return tags
        }

        val created = createNewLabels(names, tags)

        val joined = mutableListOf<LabelEntity>()
        joined.addAll(tags)
        joined.addAll(created)
        return joined
    }

    private fun createNewLabels(names: List<String>, tags: List<LabelEntity>): Iterable<LabelEntity> {
        val now = Date()
        val map: Map<String, LabelEntity> = tags.map { it.name to it }.toMap()
        val created = names
            .filter { map[toName(it)] == null }
            .map { createLabel(it, now) }
            .associateBy { it.name } // To prevent name duplication

        return dao.saveAll(created.values)
    }

    private fun createLabel(name: String, now: Date): LabelEntity {
        return LabelEntity(
            name = toName(name),
            displayName = toDisplayName(name),
            createdAt = now,
        )
    }

    private fun toDisplayName(name: String): String {
        return WordUtils.capitalize(name)
    }

    private fun toName(name: String): String {
        return toAscii(name)
            .replace(FILTER1, SEPARATOR)
            .replace(FILTER2, SEPARATOR)
            .lowercase()
    }

    private fun toAscii(str: String): String {
        val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }
}
