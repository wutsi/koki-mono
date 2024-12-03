package com.wutsi.koki.portal.service

import com.wutsi.koki.portal.mapper.UserMapper
import com.wutsi.koki.portal.model.RoleModel
import com.wutsi.koki.portal.model.UserModel
import com.wutsi.koki.sdk.KokiUser
import org.springframework.stereotype.Service

@Service
class UserService(
    private val koki: KokiUser,
    private val mapper: UserMapper,
) {
    fun user(id: Long): UserModel {
        return mapper.toUserModel(koki.user(id).user)
    }

    fun users(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        roleIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): List<UserModel> {
        val users = koki.users(
            keyword = keyword,
            ids = ids,
            roleIds = roleIds,
            limit = limit,
            offset = offset
        ).users

        return users.map { user -> mapper.toUserModel(user) }
    }

    fun role(id: Long): RoleModel {
        return roles(listOf(id)).first()
    }

    fun roles(
        ids: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): List<RoleModel> {
        val roles = koki.roles(
            ids = ids,
            limit = limit,
            offset = offset
        ).roles
        return roles.map { role -> mapper.toRoleModel(role) }
    }
}
