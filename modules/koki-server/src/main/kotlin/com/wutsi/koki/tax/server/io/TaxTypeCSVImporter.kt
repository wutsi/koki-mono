package com.wutsi.koki.tax.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.error.exception.WutsiException
import com.wutsi.koki.tax.server.domain.TaxTypeEntity
import com.wutsi.koki.tax.server.service.TaxTypeService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class TaxTypeCSVImporter(
    private val service: TaxTypeService
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TaxTypeCSVImporter::class.java)
    }

    fun import(input: InputStream, tenantId: Long): ImportResponse {
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder
                .create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(*TaxTypeEntity.CSV_HEADERS.toTypedArray())
                .setTrim(true)
                .build(),
        )
        var added: Int = 0
        var updated: Int = 0
        var errorMessages: MutableList<ImportMessage> = mutableListOf()
        var row: Int = 0
        val names = mutableListOf<String>()
        parser.use {
            // Add/Update
            for (record in parser) {
                row++
                val name = record.get(TaxTypeEntity.CSV_HEADER_NAME)
                try {
                    validate(record)
                    val role = findTaxType(tenantId, record)
                    if (role == null) {
                        LOGGER.info("$row - Adding '$name'")
                        add(record, tenantId)
                        added++
                    } else {
                        LOGGER.info("$row - Updating '$name'")
                        update(role, record)
                        updated++
                    }
                    names.add(name.lowercase())
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
        val name = record.get(TaxTypeEntity.CSV_HEADER_NAME)
        if (name.isNullOrEmpty()) {
            throw BadRequestException(error = Error(code = ErrorCode.TAX_TYPE_NAME_MISSING))
        }
    }

    private fun findTaxType(tenantId: Long, record: CSVRecord): TaxTypeEntity? {
        try {
            val name = record.get(TaxTypeEntity.CSV_HEADER_NAME)
            return service.getByName(name, tenantId)
        } catch (ex: NotFoundException) {
            return null
        }
    }

    private fun add(record: CSVRecord, tenantId: Long) {
        service.save(
            TaxTypeEntity(
                tenantId = tenantId,
                name = record.get(TaxTypeEntity.CSV_HEADER_NAME),
                title = record.get(TaxTypeEntity.CSV_HEADER_TITLE).ifEmpty { null },
                description = record.get(TaxTypeEntity.CSV_HEADER_DESCRIPTION).ifEmpty { null },
                active = record.get(TaxTypeEntity.CSV_HEADER_ACTIVE).lowercase() == "yes",
            )
        )
    }

    private fun update(taxType: TaxTypeEntity, record: CSVRecord) {
        taxType.name = record.get(TaxTypeEntity.CSV_HEADER_NAME)
        taxType.title = record.get(TaxTypeEntity.CSV_HEADER_TITLE).ifEmpty { null }
        taxType.description = record.get(TaxTypeEntity.CSV_HEADER_DESCRIPTION).ifEmpty { null }
        taxType.active = record.get(TaxTypeEntity.CSV_HEADER_ACTIVE).equals("yes", true)
        service.save(taxType)
    }
}
