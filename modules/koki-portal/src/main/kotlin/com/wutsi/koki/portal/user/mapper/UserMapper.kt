package com.wutsi.koki.portal.user.mapper

import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.module.model.PermissionModel
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import org.springframework.stereotype.Service

@Service
class UserMapper : TenantAwareMapper() {
    fun toUserModel(entity: User, roles: List<RoleModel>): UserModel {
        return UserModel(
            id = entity.id,
            email = entity.email,
            displayName = entity.displayName,
            status = entity.status,
            roles = roles,
        )
    }

    fun toUserModel(entity: UserSummary): UserModel {
        return UserModel(
            id = entity.id,
            email = entity.email,
            displayName = entity.displayName,
            status = entity.status,
        )
    }

    fun toRoleModel(entity: Role, permissions: Map<Long, PermissionModel>): RoleModel {
        val fmt = createDateTimeFormat()
        return RoleModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: "",
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            permissions = entity.permissionIds.mapNotNull { id -> permissions[id] },
        )
    }
}
