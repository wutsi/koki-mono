package com.wutsi.koki.tenant.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.error.exception.WutsiException
import com.wutsi.koki.tenant.dto.AttributeType
import com.wutsi.koki.tenant.server.domain.AttributeEntity
import com.wutsi.koki.tenant.server.domain.AttributeEntity.Companion.CSV_HEADERS
import com.wutsi.koki.tenant.server.domain.AttributeEntity.Companion.CSV_HEADER_ACTIVE
import com.wutsi.koki.tenant.server.domain.AttributeEntity.Companion.CSV_HEADER_CHOICES
import com.wutsi.koki.tenant.server.domain.AttributeEntity.Companion.CSV_HEADER_DESCRIPTION
import com.wutsi.koki.tenant.server.domain.AttributeEntity.Companion.CSV_HEADER_LABEL
import com.wutsi.koki.tenant.server.domain.AttributeEntity.Companion.CSV_HEADER_NAME
import com.wutsi.koki.tenant.server.domain.AttributeEntity.Companion.CSV_HEADER_TYPE
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.AttributeService
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class AttributeCSVImporter(
    private val service: AttributeService,
    private val tenantService: TenantService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AttributeCSVImporter::class.java)
    }

    fun import(tenantId: Long, input: InputStream): ImportResponse {
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder
                .create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(*CSV_HEADERS.toTypedArray())
                .build(),
        )
        mutableListOf<ImportMessage>()
        var added: Int = 0
        var updated: Int = 0
        var errorMessages: MutableList<ImportMessage> = mutableListOf()
        var row: Int = 0
        parser.use {
            val tenant = tenantService.get(tenantId)
            for (record in parser) {
                row++
                val name = record.get(CSV_HEADER_NAME)
                try {
                    validate(record)
                    val attribute = findAttribute(tenantId, record)
                    if (attribute == null) {
                        LOGGER.info("$row - Adding '$name'")
                        add(tenant, record)
                        added++
                    } else {
                        LOGGER.info("$row - Updating '$name'")
                        update(attribute, record)
                        updated++
                    }
                } catch (ex: WutsiException) {
                    errorMessages.add(
                        ImportMessage(row.toString(), ex.error.code, ex.error.message)
                    )
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
        val name = record.get(CSV_HEADER_NAME)?.trim()
        if (name.isNullOrEmpty()) {
            throw BadRequestException(error = Error(code = ErrorCode.ATTRIBUTE_NAME_MISSING))
        }

        // Type
        try {
            val type = AttributeType.valueOf(record.get(CSV_HEADER_TYPE).trim().uppercase())
            if (type == AttributeType.UNKNOWN) {
                throw BadRequestException(error = Error(code = ErrorCode.ATTRIBUTE_TYPE_INVALID))
            }
        } catch (ex: Exception) {
            throw BadRequestException(error = Error(code = ErrorCode.ATTRIBUTE_TYPE_INVALID, message = ex.message))
        }
    }

    private fun findAttribute(tenantId: Long, record: CSVRecord): AttributeEntity? {
        try {
            val name = record.get(CSV_HEADER_NAME).trim()
            return service.getByName(name, tenantId)
        } catch (ex: NotFoundException) {
            return null
        }
    }

    private fun add(tenant: TenantEntity, record: CSVRecord) {
        service.save(
            AttributeEntity(
                tenant = tenant,
                name = record.get(CSV_HEADER_NAME).trim(),
                type = AttributeType.valueOf(record.get(CSV_HEADER_TYPE).trim().uppercase()),
                label = record.get(CSV_HEADER_LABEL)?.ifEmpty { null },
                description = record.get(CSV_HEADER_DESCRIPTION)?.ifEmpty { null },
                active = record.get(CSV_HEADER_ACTIVE)?.trim()?.lowercase() == "yes",
                choices = record.get(CSV_HEADER_CHOICES)?.trim()?.replace('|', '\n')?.ifEmpty { null },
            )
        )
    }

    private fun update(attribute: AttributeEntity, record: CSVRecord) {
        attribute.name = record.get(CSV_HEADER_NAME).trim()
        attribute.type = AttributeType.valueOf(record.get(CSV_HEADER_TYPE).trim().uppercase())
        attribute.label = record.get(CSV_HEADER_LABEL)?.ifEmpty { null }
        attribute.description = record.get(CSV_HEADER_DESCRIPTION)?.ifEmpty { null }
        attribute.active = record.get(CSV_HEADER_ACTIVE)?.trim()?.lowercase().equals("yes")
        attribute.choices = record.get(CSV_HEADER_CHOICES)?.trim()?.replace('|', '\n')?.ifEmpty { null }
        service.save(attribute)
    }
}
