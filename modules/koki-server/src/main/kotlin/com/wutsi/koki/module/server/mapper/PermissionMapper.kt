package com.wutsi.koki.module.server.mapper

import com.wutsi.koki.module.dto.Permission
import com.wutsi.koki.module.server.domain.PermissionEntity
import org.springframework.stereotype.Service

@Service
class PermissionMapper {
    fun toPermission(entity: PermissionEntity): Permission {
        return Permission(
            id = entity.id!!,
            moduleId = entity.moduleId,
            name = entity.name,
            description = entity.description,
        )
    }
}
