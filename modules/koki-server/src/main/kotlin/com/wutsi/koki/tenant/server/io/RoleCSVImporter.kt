package com.wutsi.koki.tenant.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.error.exception.WutsiException
import com.wutsi.koki.tenant.server.domain.RoleEntity
import com.wutsi.koki.tenant.server.domain.RoleEntity.Companion.CSV_HEADERS
import com.wutsi.koki.tenant.server.domain.RoleEntity.Companion.CSV_HEADER_ACTIVE
import com.wutsi.koki.tenant.server.domain.RoleEntity.Companion.CSV_HEADER_DESCRIPTION
import com.wutsi.koki.tenant.server.domain.RoleEntity.Companion.CSV_HEADER_NAME
import com.wutsi.koki.tenant.server.domain.RoleEntity.Companion.CSV_HEADER_TITLE
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.RoleService
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class RoleCSVImporter(
    private val service: RoleService,
    private val tenantService: TenantService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RoleCSVImporter::class.java)
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
                .setTrim(true)
                .get(),
        )
        var added: Int = 0
        var updated: Int = 0
        var errorMessages: MutableList<ImportMessage> = mutableListOf()
        var row: Int = 0
        val names = mutableListOf<String>()
        parser.use {
            // Add/Update
            val tenant = tenantService.get(tenantId)
            for (record in parser) {
                row++
                val name = record.get(CSV_HEADER_NAME)
                try {
                    validate(record)
                    val role = findRole(tenantId, record)
                    if (role == null) {
                        LOGGER.info("$row - Adding '$name'")
                        add(tenant, record)
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
            service.search(tenantId = tenantId, limit = Integer.MAX_VALUE).forEach { role ->
                if (!names.contains(role.name.lowercase()) && role.active) {
                    LOGGER.info("Deactivating '${role.name}'")
                    role.active = false
                    updated++
                    service.save(role)
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
        val name = record.get(CSV_HEADER_NAME)
        if (name.isNullOrEmpty()) {
            throw BadRequestException(error = Error(code = ErrorCode.ROLE_NAME_MISSING))
        }
    }

    private fun findRole(tenantId: Long, record: CSVRecord): RoleEntity? {
        try {
            val name = record.get(CSV_HEADER_NAME)
            return service.getByName(name, tenantId)
        } catch (ex: NotFoundException) {
            return null
        }
    }

    private fun add(tenant: TenantEntity, record: CSVRecord): RoleEntity {
        return service.save(
            RoleEntity(
                tenantId = tenant.id!!,
                name = record.get(CSV_HEADER_NAME),
                title = record.get(CSV_HEADER_TITLE).ifEmpty { null },
                description = record.get(CSV_HEADER_DESCRIPTION).ifEmpty { null },
                active = record.get(CSV_HEADER_ACTIVE).lowercase() == "yes",
            )
        )
    }

    private fun update(role: RoleEntity, record: CSVRecord) {
        role.name = record.get(CSV_HEADER_NAME)
        role.title = record.get(CSV_HEADER_TITLE).ifEmpty { null }
        role.description = record.get(CSV_HEADER_DESCRIPTION).ifEmpty { null }
        role.active = record.get(CSV_HEADER_ACTIVE).equals("yes", true)
        service.save(role)
    }
}
