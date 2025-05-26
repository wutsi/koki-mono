package com.wutsi.koki.room.web.tenant.mapper

import com.wutsi.koki.room.web.tenant.model.TypeModel
import com.wutsi.koki.tenant.dto.Type
import com.wutsi.koki.tenant.dto.TypeSummary
import org.springframework.stereotype.Service

@Service
class TypeMapper {
    fun toTypeModel(entity: Type): TypeModel {
        return TypeModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: entity.name,
            active = entity.active,
            objectType = entity.objectType,
            description = entity.description,
        )
    }

    fun toTypeModel(entity: TypeSummary): TypeModel {
        return TypeModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: entity.name,
            objectType = entity.objectType,
            active = entity.active,
        )
    }
}
