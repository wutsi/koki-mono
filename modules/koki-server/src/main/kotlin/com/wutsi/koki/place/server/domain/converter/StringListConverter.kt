package com.wutsi.koki.place.server.domain.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * Converts a List<String> to a comma-separated TEXT field in the database
 */
@Converter
class StringListConverter : AttributeConverter<List<String>?, String?> {
    override fun convertToDatabaseColumn(attribute: List<String>?): String? {
        return attribute?.joinToString(",")
    }

    override fun convertToEntityAttribute(dbData: String?): List<String>? {
        return dbData?.split(",")?.filter { it.isNotBlank() }
    }
}
