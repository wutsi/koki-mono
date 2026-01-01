package com.wutsi.koki.place.server.domain.converter

import com.wutsi.koki.place.dto.Diploma
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * Converts a List<Diploma> to a comma-separated TEXT field in the database
 */
@Converter
class DiplomaListConverter : AttributeConverter<List<Diploma>?, String?> {
    override fun convertToDatabaseColumn(attribute: List<Diploma>?): String? {
        return attribute?.joinToString(",") { it.name }
    }

    override fun convertToEntityAttribute(dbData: String?): List<Diploma>? {
        return dbData?.split(",")
            ?.filter { it.isNotBlank() }
            ?.mapNotNull {
                try {
                    Diploma.valueOf(it)
                } catch (_: IllegalArgumentException) {
                    null
                }
            }
    }
}
