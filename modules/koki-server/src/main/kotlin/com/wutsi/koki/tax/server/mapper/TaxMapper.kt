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
            description = entity.description,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            startAt = entity.startAt,
            dueAt = entity.dueAt,
            createdById = entity.createdById,
            modifiedById = entity.modifiedById,
            accountantId = entity.accountantId,
            accountId = entity.accountId,
            taxTypeId = entity.taxTypeId,
            fiscalYear = entity.fiscalYear,
            status = entity.status,
        )
    }

    fun toTaxSummary(entity: TaxEntity): TaxSummary {
        return TaxSummary(
            id = entity.id!!,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            startAt = entity.startAt,
            dueAt = entity.dueAt,
            accountantId = entity.accountantId,
            accountId = entity.accountId,
            taxTypeId = entity.taxTypeId,
            fiscalYear = entity.fiscalYear,
            status = entity.status,
        )
    }
}
