package com.wutsi.koki.product.server.mapper

import com.wutsi.koki.product.dto.Price
import com.wutsi.koki.product.dto.PriceSummary
import com.wutsi.koki.product.server.domain.PriceEntity
import org.springframework.stereotype.Service

@Service
class PriceMapper{
    fun toPrice(entity: PriceEntity): Price{
        return Price(
            id = entity.id!!,
            name = entity.name,
            amount = entity.amount,
            currency = entity.currency,
            active = entity.active,
            startAt = entity.startAt,
            endAt = entity.endAt,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            modifiedAt = entity.modifiedAt,
            modifiedById = entity.modifiedById,
        )
    }

    fun toPriceSummary(entity: PriceEntity): PriceSummary{
        return PriceSummary(
            id = entity.id!!,
            name = entity.name,
            amount = entity.amount,
            currency = entity.currency,
            active = entity.active,
            startAt = entity.startAt,
            endAt = entity.endAt,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            modifiedAt = entity.modifiedAt,
            modifiedById = entity.modifiedById,
        )
    }
}
