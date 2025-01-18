package com.wutsi.koki.portal.user.mapper

import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import org.springframework.stereotype.Service

@Service
class UserMapper {
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

    fun toRoleModel(role: Role): RoleModel {
        return RoleModel(
            id = role.id,
            name = role.name,
            title = role.title ?: "",
        )
    }
}
