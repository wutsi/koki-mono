package com.wutsi.koki.tenant.server.io

import com.wutsi.koki.common.dto.ErrorCode
import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.tenant.server.domain.RoleEntity
import com.wutsi.koki.tenant.server.domain.RoleEntity.Companion.CSV_HEADERS
import com.wutsi.koki.tenant.server.domain.RoleEntity.Companion.CSV_HEADER_ACTIVE
import com.wutsi.koki.tenant.server.domain.RoleEntity.Companion.CSV_HEADER_DESCRIPTION
import com.wutsi.koki.tenant.server.domain.RoleEntity.Companion.CSV_HEADER_NAME
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.RoleService
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.error.exception.WutsiException
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
            throw BadRequestException(error = Error(code = ErrorCode.ROLE_NAME_MISSING))
        }
    }

    private fun findRole(tenantId: Long, record: CSVRecord): RoleEntity? {
        try {
            val name = record.get(CSV_HEADER_NAME).trim()
            return service.findByName(tenantId, name)
        } catch (ex: NotFoundException) {
            return null
        }
    }

    private fun add(tenant: TenantEntity, record: CSVRecord) {
        service.save(
            RoleEntity(
                tenant = tenant,
                name = record.get(CSV_HEADER_NAME).trim(),
                description = record.get(CSV_HEADER_DESCRIPTION)?.ifEmpty { null },
                active = record.get(CSV_HEADER_ACTIVE)?.trim()?.lowercase() == "yes",
            )
        )
    }

    private fun update(role: RoleEntity, record: CSVRecord) {
        role.name = record.get(CSV_HEADER_NAME).trim()
        role.description = record.get(CSV_HEADER_DESCRIPTION)?.ifEmpty { null }
        role.active = record.get(CSV_HEADER_ACTIVE)?.trim()?.lowercase().equals("yes")
        service.save(role)
    }
}