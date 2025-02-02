package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.tenant.dto.Type
import com.wutsi.koki.tenant.dto.TypeSummary
import com.wutsi.koki.tenant.server.domain.TypeEntity
import org.springframework.stereotype.Service

@Service
class TypeMapper {
    fun toType(entity: TypeEntity): Type {
        return Type(
            id = entity.id!!,
            objectType = entity.objectType,
            name = entity.name,
            title = entity.title,
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }

    fun toTypeSummary(entity: TypeEntity): TypeSummary {
        return TypeSummary(
            id = entity.id!!,
            objectType = entity.objectType,
            name = entity.name,
            title = entity.title,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
