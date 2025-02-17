package com.wutsi.koki.portal.refdata.mapper

import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.refdata.model.CategoryModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.refdata.model.UnitModel
import com.wutsi.koki.refdata.dto.Category
import com.wutsi.koki.refdata.dto.Location
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

    fun toLocationModel(entity: Location): LocationModel {
        return LocationModel(
            id = entity.id,
            name = entity.name,
            parentId = entity.parentId,
            type = entity.type,
            country = entity.country,
        )
    }

    fun toCategoryModel(entity: Category): CategoryModel {
        return CategoryModel(
            id = entity.id,
            name = entity.name,
            parentId = entity.parentId,
            type = entity.type,
            level = entity.level,
            active = entity.active,
            longName = entity.longName,
        )
    }
}
