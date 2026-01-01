package com.wutsi.koki.place.server.domain.converter

import com.wutsi.koki.place.dto.SchoolLevel
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * Converts a List<SchoolLevel> to a comma-separated TEXT field in the database
 */
@Converter
class SchoolLevelListConverter : AttributeConverter<List<SchoolLevel>?, String?> {
    override fun convertToDatabaseColumn(attribute: List<SchoolLevel>?): String? {
        return attribute?.joinToString(",") { it.name }
    }

    override fun convertToEntityAttribute(dbData: String?): List<SchoolLevel>? {
        return dbData?.split(",")
            ?.filter { it.isNotBlank() }
            ?.mapNotNull {
                try {
                    SchoolLevel.valueOf(it)
                } catch (_: IllegalArgumentException) {
                    null
                }
            }
    }
}
