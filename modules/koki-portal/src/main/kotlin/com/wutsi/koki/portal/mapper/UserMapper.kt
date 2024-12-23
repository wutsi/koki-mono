package com.wutsi.koki.portal.mapper

import com.wutsi.koki.portal.model.RoleModel
import com.wutsi.koki.portal.model.UserModel
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import org.springframework.stereotype.Service

@Service
class UserMapper {
    fun toUserModel(entity: User): UserModel {
        return UserModel(
            id = entity.id,
            email = entity.email,
            displayName = entity.displayName,
            roles = entity.roles.map { role -> toRoleModel(role) },
            status = entity.status,
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

    fun toRoleModel(role: Role): RoleModel {
        return RoleModel(
            id = role.id,
            name = role.name,
            title = role.title ?: "",
        )
    }
}
