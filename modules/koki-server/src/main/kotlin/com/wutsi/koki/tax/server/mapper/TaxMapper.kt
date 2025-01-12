package com.wutsi.koki.tax.server.mapper

import com.wutsi.koki.tax.dto.TaxType
import com.wutsi.koki.tax.dto.TaxTypeSummary
import com.wutsi.koki.tax.server.domain.TaxTypeEntity
import org.springframework.stereotype.Service

@Service
class TaxTypeMapper {
    fun toTaxType(entity: TaxTypeEntity): TaxType {
        return TaxType(
            id = entity.id!!,
            name = entity.name,
            title = entity.title,
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }

    fun toTaxTypeSummary(entity: TaxTypeEntity): TaxTypeSummary {
        return TaxTypeSummary(
            id = entity.id!!,
            name = entity.name,
            title = entity.title,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
