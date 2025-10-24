package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.server.domain.RoleEntity
import org.springframework.stereotype.Service

@Service
class RoleMapper {
    fun toRole(entity: RoleEntity) = Role(id = entity.id!!,
        name = entity.name,
        title = entity.title,
        description = entity.description,
        active = entity.active,
        createdAt = entity.createdAt,
        createdById = entity.createdById,
        modifiedAt = entity.modifiedAt,
        modifiedById = entity.modifiedById,
        permissionIds = entity.permissions.mapNotNull { permission -> permission.id }
    )
}
