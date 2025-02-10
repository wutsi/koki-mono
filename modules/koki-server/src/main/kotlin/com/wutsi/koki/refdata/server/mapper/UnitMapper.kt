package com.wutsi.koki.refdata.server.mapper

import com.wutsi.koki.refdata.server.domain.UnitEntity
import org.springframework.stereotype.Service

@Service
class UnitMapper {
    fun toUnit(entity: UnitEntity): com.wutsi.koki.refdata.dto.Unit {
        return com.wutsi.koki.refdata.dto.Unit(
            id = entity.id!!,
            name = entity.name,
            abbreviation = entity.abbreviation,
        )
    }
}
