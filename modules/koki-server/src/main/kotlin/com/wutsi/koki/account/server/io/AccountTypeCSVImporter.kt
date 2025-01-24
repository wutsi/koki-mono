package com.wutsi.koki.account.server.io

import com.wutsi.koki.account.server.domain.AccountTypeEntity
import com.wutsi.koki.account.server.service.AccountTypeService
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
class AccountTypeCSVImporter(
    private val service: AccountTypeService
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AccountTypeCSVImporter::class.java)
    }

    fun import(input: InputStream, tenantId: Long): ImportResponse {
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder
                .create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(*AccountTypeEntity.CSV_HEADERS.toTypedArray())
                .setTrim(true)
                .build(),
        )
        var added: Int = 0
        var updated: Int = 0
        var errorMessages: MutableList<ImportMessage> = mutableListOf()
        var row: Int = 0
        val names = mutableListOf<String>()
        parser.use {
            for (record in parser) {
                row++
                val name = record.get(AccountTypeEntity.CSV_HEADER_NAME)
                try {
                    validate(record)
                    var type = findAccountType(tenantId, record)
                    if (type == null) {
                        LOGGER.info("$row - Adding '$name'")
                        type = add(record, tenantId)
                        added++
                    } else {
                        LOGGER.info("$row - Updating '$name'")
                        update(type, record)
                        updated++
                    }
                    names.add(type.name)
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
                    LOGGER.info("Deactivating ${type.name}")
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
        val name = record.get(AccountTypeEntity.CSV_HEADER_NAME)
        if (name.isNullOrEmpty()) {
            throw BadRequestException(error = Error(code = ErrorCode.ACCOUNT_TYPE_NAME_MISSING))
        }
    }

    private fun findAccountType(tenantId: Long, record: CSVRecord): AccountTypeEntity? {
        try {
            val name = record.get(AccountTypeEntity.CSV_HEADER_NAME)
            return service.getByName(name, tenantId)
        } catch (ex: NotFoundException) {
            return null
        }
    }

    private fun add(record: CSVRecord, tenantId: Long): AccountTypeEntity {
        return service.save(
            AccountTypeEntity(
                tenantId = tenantId,
                name = record.get(AccountTypeEntity.CSV_HEADER_NAME),
                title = record.get(AccountTypeEntity.CSV_HEADER_TITLE).ifEmpty { null },
                description = record.get(AccountTypeEntity.CSV_HEADER_DESCRIPTION).ifEmpty { null },
                active = record.get(AccountTypeEntity.CSV_HEADER_ACTIVE).lowercase() == "yes",
            )
        )
    }

    private fun update(accountType: AccountTypeEntity, record: CSVRecord) {
        accountType.name = record.get(AccountTypeEntity.CSV_HEADER_NAME)
        accountType.title = record.get(AccountTypeEntity.CSV_HEADER_TITLE).ifEmpty { null }
        accountType.description = record.get(AccountTypeEntity.CSV_HEADER_DESCRIPTION).ifEmpty { null }
        accountType.active = record.get(AccountTypeEntity.CSV_HEADER_ACTIVE).equals("yes", true)
        service.save(accountType)
    }
}
