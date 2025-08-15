package com.wutsi.koki.portal.user.mapper

import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.module.model.PermissionModel
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.tenant.dto.Role
import org.springframework.stereotype.Service

@Service
class RoleMapper : TenantAwareMapper() {
    fun toRoleModel(entity: Role, permissions: Map<Long, PermissionModel>): RoleModel {
        val fmt = createDateTimeFormat()
        return RoleModel(
            id = entity.id,
            name = entity.name,
            title = entity.title?.trim()?.ifEmpty { null },
            description = entity.description?.trim()?.ifEmpty { null },
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            permissions = entity.permissionIds
                .mapNotNull { id -> permissions[id] }
                .sortedBy { permission -> permission.name },
        )
    }
}
