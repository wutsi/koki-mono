package com.wutsi.koki.contact.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.contact.server.domain.ContactTypeEntity
import com.wutsi.koki.contact.server.service.ContactTypeService
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
class ContactTypeCSVImporter(
    private val service: ContactTypeService
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ContactTypeCSVImporter::class.java)
    }

    fun import(input: InputStream, tenantId: Long): ImportResponse {
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder
                .create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(*ContactTypeEntity.CSV_HEADERS.toTypedArray())
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
                val name = record.get(ContactTypeEntity.CSV_HEADER_NAME)
                try {
                    validate(record)
                    val role = findContactType(tenantId, record)
                    if (role == null) {
                        LOGGER.info("$row - Adding '$name'")
                        add(record, tenantId)
                        added++
                    } else {
                        LOGGER.info("$row - Updating '$name'")
                        update(role, record)
                        updated++
                    }
                } catch (ex: WutsiException) {
                    errorMessages.add(
                        ImportMessage(row.toString(), ex.error.code, ex.error.message)
                    )
                } catch (ex: Exception) {
                    errorMessages.add(
                        ImportMessage(row.toString(), ErrorCode.IMPORT_ERROR, ex.message)
                    )
                }
            }

            // Deactivate others
            service.search(tenantId = tenantId, limit = Integer.MAX_VALUE).forEach { type ->
                if (!names.contains(type.name.lowercase()) && type.active) {
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
        val name = record.get(ContactTypeEntity.CSV_HEADER_NAME)
        if (name.isNullOrEmpty()) {
            throw BadRequestException(error = Error(code = ErrorCode.CONTACT_TYPE_NAME_MISSING))
        }
    }

    private fun findContactType(tenantId: Long, record: CSVRecord): ContactTypeEntity? {
        try {
            val name = record.get(ContactTypeEntity.CSV_HEADER_NAME)
            return service.getByName(name, tenantId)
        } catch (ex: NotFoundException) {
            return null
        }
    }

    private fun add(record: CSVRecord, tenantId: Long) {
        service.save(
            ContactTypeEntity(
                tenantId = tenantId,
                name = record.get(ContactTypeEntity.CSV_HEADER_NAME),
                title = record.get(ContactTypeEntity.CSV_HEADER_TITLE).ifEmpty { null },
                description = record.get(ContactTypeEntity.CSV_HEADER_DESCRIPTION).ifEmpty { null },
                active = record.get(ContactTypeEntity.CSV_HEADER_ACTIVE).lowercase() == "yes",
            )
        )
    }

    private fun update(contactType: ContactTypeEntity, record: CSVRecord) {
        contactType.name = record.get(ContactTypeEntity.CSV_HEADER_NAME)
        contactType.title = record.get(ContactTypeEntity.CSV_HEADER_TITLE).ifEmpty { null }
        contactType.description = record.get(ContactTypeEntity.CSV_HEADER_DESCRIPTION).ifEmpty { null }
        contactType.active = record.get(ContactTypeEntity.CSV_HEADER_ACTIVE).equals("yes", true)
        service.save(contactType)
    }
}
