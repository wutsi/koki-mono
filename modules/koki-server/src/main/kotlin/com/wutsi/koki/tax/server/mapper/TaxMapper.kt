package com.wutsi.koki.tax.server.mapper

import com.wutsi.koki.tax.dto.Tax
import com.wutsi.koki.tax.dto.TaxSummary
import com.wutsi.koki.tax.server.domain.TaxEntity
import org.springframework.stereotype.Service

@Service
class TaxMapper {
    fun toTax(entity: TaxEntity): Tax {
        return Tax(
            id = entity.id!!,
            taxTypeId = entity.taxTypeId,
            fiscalYear = entity.fiscalYear,
            status = entity.status,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            startAt = entity.startAt,
            dueAt = entity.dueAt,
            createdById = entity.createdById,
            modifiedById = entity.modifiedById,
            accountantId = entity.accountantId,
            technicianId = entity.technicianId,
            assigneeId = entity.assigneeId,
            accountId = entity.accountId,
            description = entity.description,
        )
    }

    fun toTaxSummary(entity: TaxEntity): TaxSummary {
        return TaxSummary(
            id = entity.id!!,
            taxTypeId = entity.taxTypeId,
            fiscalYear = entity.fiscalYear,
            status = entity.status,
            accountId = entity.accountId,
            accountantId = entity.accountantId,
            technicianId = entity.technicianId,
            assigneeId = entity.assigneeId,
            createdById = entity.createdById,
            modifiedById = entity.modifiedById,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            startAt = entity.startAt,
            dueAt = entity.dueAt,
        )
    }
}
