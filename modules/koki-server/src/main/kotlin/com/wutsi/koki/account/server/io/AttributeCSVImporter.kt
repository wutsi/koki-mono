package com.wutsi.koki.account.server.io

import com.wutsi.koki.account.dto.AttributeType
import com.wutsi.koki.account.server.domain.AttributeEntity
import com.wutsi.koki.account.server.service.AttributeService
import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.error.exception.WutsiException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class AttributeCSVImporter(private val service: AttributeService) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AttributeCSVImporter::class.java)
    }

    fun import(input: InputStream, tenantId: Long): ImportResponse {
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder
                .create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(*AttributeEntity.CSV_HEADERS.toTypedArray())
                .setTrim(true)
                .build(),
        )
        val names = mutableListOf<String>()
        var added: Int = 0
        var updated: Int = 0
        var errorMessages: MutableList<ImportMessage> = mutableListOf()
        var row: Int = 0
        parser.use {
            // Add/Update
            for (record in parser) {
                row++
                val name = record.get(AttributeEntity.CSV_HEADER_NAME)
                try {
                    validate(record)
                    val attribute = findAttribute(tenantId, record)
                    if (attribute == null) {
                        LOGGER.info("$row - Adding '$name'")
                        add(tenantId, record)
                        added++
                    } else {
                        LOGGER.info("$row - Updating '$name'")
                        update(attribute, record)
                        updated++
                    }
                    names.add(name.lowercase())
                } catch (ex: WutsiException) {
                    errorMessages.add(
                        ImportMessage(row.toString(), ex.error.code, ex.error.message)
                    )
                }
            }

            // Deactivate others
            service.search(tenantId = tenantId, limit = Integer.MAX_VALUE).forEach { type ->
                if (!names.contains(type.name.lowercase()) && type.active) {
                    LOGGER.info("$row - Deactivating '${type.name}'")
                    type.active = false
                    updated++
                    service.save(type)
                }
            }
        }

        return ImportResponse(
            added = added,
            updated = updated,
            errors = errorMessages.size,
            errorMessages = errorMessages
        )
    }

    private fun validate(record: CSVRecord) {
        // Name
        val name = record.get(AttributeEntity.CSV_HEADER_NAME)
        if (name.isEmpty()) {
            throw BadRequestException(error = Error(code = ErrorCode.ATTRIBUTE_NAME_MISSING))
        }

        // Type
        try {
            val type = AttributeType.valueOf(record.get(AttributeEntity.CSV_HEADER_TYPE).uppercase())
            if (type == AttributeType.UNKNOWN) {
                throw BadRequestException(error = Error(code = ErrorCode.ATTRIBUTE_TYPE_INVALID))
            }
        } catch (ex: Exception) {
            throw BadRequestException(error = Error(code = ErrorCode.ATTRIBUTE_TYPE_INVALID, message = ex.message))
        }
    }

    private fun findAttribute(tenantId: Long, record: CSVRecord): AttributeEntity? {
        try {
            val name = record.get(AttributeEntity.CSV_HEADER_NAME)
            return service.getByName(name, tenantId)
        } catch (ex: NotFoundException) {
            return null
        }
    }

    private fun add(tenantId: Long, record: CSVRecord): AttributeEntity {
        return service.save(
            AttributeEntity(
                tenantId = tenantId,
                name = record.get(AttributeEntity.CSV_HEADER_NAME),
                type = AttributeType.valueOf(record.get(AttributeEntity.CSV_HEADER_TYPE).uppercase()),
                label = record.get(AttributeEntity.CSV_HEADER_LABEL).ifEmpty { null },
                description = record.get(AttributeEntity.CSV_HEADER_DESCRIPTION).ifEmpty { null },
                active = record.get(AttributeEntity.CSV_HEADER_ACTIVE).lowercase() == "yes",
                required = record.get(AttributeEntity.CSV_HEADER_REQUIRED).lowercase() == "yes",
                choices = record.get(AttributeEntity.CSV_HEADER_CHOICES).replace('|', '\n').ifEmpty { null },
            )
        )
    }

    private fun update(attribute: AttributeEntity, record: CSVRecord) {
        attribute.name = record.get(AttributeEntity.CSV_HEADER_NAME)
        attribute.type = AttributeType.valueOf(record.get(AttributeEntity.CSV_HEADER_TYPE).uppercase())
        attribute.label = record.get(AttributeEntity.CSV_HEADER_LABEL).ifEmpty { null }
        attribute.description = record.get(AttributeEntity.CSV_HEADER_DESCRIPTION).ifEmpty { null }
        attribute.required = record.get(AttributeEntity.CSV_HEADER_REQUIRED).lowercase().equals("yes")
        attribute.active = record.get(AttributeEntity.CSV_HEADER_ACTIVE).lowercase().equals("yes")
        attribute.choices = record.get(AttributeEntity.CSV_HEADER_CHOICES).replace('|', '\n').ifEmpty { null }
        service.save(attribute)
    }
}
