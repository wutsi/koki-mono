package com.wutsi.koki.product.server.mapper

import com.wutsi.koki.product.server.domain.UnitEntity
import org.springframework.stereotype.Service

@Service
class UnitMapper {
    fun toUnit(entity: UnitEntity): com.wutsi.koki.product.dto.Unit {
        return com.wutsi.koki.product.dto.Unit(
            id = entity.id!!,
            name = entity.name,
            abbreviation = entity.abbreviation,
        )
    }
}
