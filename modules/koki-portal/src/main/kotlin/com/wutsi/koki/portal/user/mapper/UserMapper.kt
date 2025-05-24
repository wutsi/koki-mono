package com.wutsi.koki.portal.user.mapper

import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.module.model.PermissionModel
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import org.springframework.stereotype.Service
import java.util.Locale
import kotlin.collections.flatMap

@Service
class UserMapper : TenantAwareMapper() {
    fun toUserModel(entity: User, roles: List<RoleModel>): UserModel {
        val fmt = createDateTimeFormat()
        return UserModel(
            id = entity.id,
            accountId = entity.accountId,
            username = entity.username,
            email = entity.email,
            displayName = entity.displayName,
            status = entity.status,
            type = entity.type,
            language = entity.language,
            languageText = entity.language?.let { lang -> Locale(lang).displayName },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            roles = roles,
            permissionNames = roles.flatMap { role -> role.permissions }
                .distinctBy { permission -> permission.id }
                .map { permission -> permission.name }
        )
    }

    fun toUserModel(entity: UserSummary): UserModel {
        val fmt = createDateTimeFormat()
        return UserModel(
            id = entity.id,
            accountId = entity.accountId,
            username = entity.username,
            email = entity.email,
            displayName = entity.displayName,
            status = entity.status,
            type = entity.type,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

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
