package com.wutsi.koki.portal.user.service

import com.wutsi.koki.portal.user.mapper.UserMapper
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.sdk.KokiUsers
import org.springframework.stereotype.Service

@Service
class UserService(
    private val koki: KokiUsers,
    private val mapper: UserMapper,
    private val roleService: RoleService
) {
    fun user(id: Long, fullGraph: Boolean = true): UserModel {
        val user = koki.user(id).user
        val roles = if (user.roleIds.isEmpty() || !fullGraph) {
            emptyList()
        } else {
            roleService.roles(user.roleIds)
        }
        return mapper.toUserModel(user, roles)
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
