package com.wutsi.koki.room.web.refdata.service

import com.wutsi.koki.refdata.dto.Category
import com.wutsi.koki.room.web.common.mapper.TenantAwareMapper
import com.wutsi.koki.room.web.refdata.model.CategoryModel
import org.springframework.stereotype.Service

@Service
class CategoryMapper : TenantAwareMapper() {
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
