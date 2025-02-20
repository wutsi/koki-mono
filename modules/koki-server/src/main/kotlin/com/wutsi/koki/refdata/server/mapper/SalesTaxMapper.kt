package com.wutsi.koki.refdata.server.mapper

import com.wutsi.koki.refdata.dto.SalesTax
import com.wutsi.koki.refdata.server.domain.SalesTaxEntity
import org.springframework.stereotype.Service

@Service
class SalesTaxMapper {
    fun toSalesTax(entity: SalesTaxEntity): SalesTax {
        return SalesTax(
            id = entity.id!!,
            name = entity.name,
            active = entity.active,
            rate = entity.rate,
            juridictionId = entity.juridiction.id,
            priority = entity.priority,
        )
    }
}
