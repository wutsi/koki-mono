package com.wutsi.koki.refdata.server.mapper

import com.wutsi.koki.refdata.dto.Category
import com.wutsi.koki.refdata.server.domain.CategoryEntity
import org.springframework.stereotype.Service

@Service
class CategoryMapper {
    fun toCategory(entity: CategoryEntity): Category {
        return Category(
            id = entity.id!!,
            name = entity.name,
            longName = entity.longName,
            type = entity.type,
            parentId = entity.parentId,
            level = entity.level,
            active = entity.active,
        )
    }
}
