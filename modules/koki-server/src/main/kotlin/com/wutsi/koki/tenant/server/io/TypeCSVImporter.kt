package com.wutsi.koki.tenant.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.error.exception.WutsiException
import com.wutsi.koki.tenant.server.domain.TypeEntity
import com.wutsi.koki.tenant.server.service.TypeService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class TypeCSVImporter(
    private val service: TypeService
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TypeCSVImporter::class.java)
    }

    fun import(
        input: InputStream,
        objectType: ObjectType,
        tenantId: Long
    ): ImportResponse {
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder
                .create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(*TypeEntity.CSV_HEADERS.toTypedArray())
                .setTrim(true)
                .get(),
        )
        val keys = mutableListOf<String>()
        var added: Int = 0
        var updated: Int = 0
        var errorMessages: MutableList<ImportMessage> = mutableListOf()
        var row: Int = 0
        parser.use {
            // Add/Update
            for (record in parser) {
                row++
                val name = record.get(TypeEntity.CSV_HEADER_NAME)
                try {
                    validate(record)
                    val type = findType(record, objectType, tenantId)
                    if (type == null) {
                        LOGGER.info("$row - Adding '$name' for $objectType")
                        add(record, objectType, tenantId)
                        added++
                    } else {
                        LOGGER.info("$row - Updating '$name' for $objectType")
                        update(type, record)
                        updated++
                    }
                    keys.add(toKey(objectType, name))
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
            service.search(
                tenantId = tenantId,
                objectType = objectType,
                limit = Integer.MAX_VALUE
            ).forEach { type ->
                if (!keys.contains(toKey(type.objectType, type.name)) && type.active) {
                    LOGGER.info("Deactivating '${type.name}' for $objectType")
                    deactivate(type)
                    updated++
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

    private fun toKey(type: ObjectType, name: String): String {
        return type.name + "-" + name.lowercase()
    }

    private fun validate(record: CSVRecord) {
        // Name
        val name = getName(record)
        if (name.isEmpty()) {
            throw BadRequestException(error = Error(code = ErrorCode.TYPE_NAME_MISSING))
        }
    }

    private fun findType(record: CSVRecord, objectType: ObjectType, tenantId: Long): TypeEntity? {
        val name = getName(record)
        return service.getByNameAndObjectType(name, objectType, tenantId)
    }

    private fun getName(record: CSVRecord): String {
        return record.get(TypeEntity.CSV_HEADER_NAME)?.trim() ?: ""
    }

    private fun add(record: CSVRecord, objectType: ObjectType, tenantId: Long): TypeEntity {
        return service.save(
            TypeEntity(
                tenantId = tenantId,
                objectType = objectType,
                name = getName(record),
                title = record.get(TypeEntity.CSV_HEADER_TITLE).trim().ifEmpty { null },
                description = record.get(TypeEntity.CSV_HEADER_DESCRIPTION).trim().ifEmpty { null },
                active = true,
            )
        )
    }

    private fun update(type: TypeEntity, record: CSVRecord) {
        type.name = getName(record)
        type.title = record.get(TypeEntity.CSV_HEADER_TITLE).ifEmpty { null }
        type.description = record.get(TypeEntity.CSV_HEADER_DESCRIPTION).ifEmpty { null }
        type.active = true
        service.save(type)
    }

    fun deactivate(type: TypeEntity) {
        type.active = false
        service.save(type)
    }
}
