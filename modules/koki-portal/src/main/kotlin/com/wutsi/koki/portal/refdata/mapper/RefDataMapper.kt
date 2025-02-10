package com.wutsi.koki.portal.refdata.mapper

import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.refdata.model.UnitModel
import com.wutsi.koki.refdata.dto.Unit
import org.springframework.stereotype.Service

@Service
class RefDataMapper : TenantAwareMapper() {
    fun toUnitModel(entity: Unit): UnitModel {
        return UnitModel(
            id = entity.id,
            name = entity.name,
            abbreviation = entity.abbreviation,
        )
    }
}
