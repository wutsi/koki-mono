package com.wutsi.koki.portal.user.service

import com.wutsi.koki.portal.user.mapper.UserMapper
import com.wutsi.koki.portal.user.model.UserForm
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.model.UserRoleForm
import com.wutsi.koki.sdk.KokiUsers
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.SetRoleListRequest
import com.wutsi.koki.tenant.dto.UpdateUserRequest
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
        permissions: List<String> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<UserModel> {
        val users = koki.users(
            keyword = keyword,
            ids = ids,
            roleIds = roleIds,
            permissions = permissions,
            limit = limit,
            offset = offset
        ).users

        return users.map { user -> mapper.toUserModel(user) }
    }

    fun create(form: UserForm): Long {
        return koki.createUser(
            CreateUserRequest(
                displayName = form.displayName,
                email = form.email,
                password = form.password,
            )
        ).userId
    }

    fun update(id: Long, form: UserForm) {
        koki.updateUser(
            id,
            UpdateUserRequest(
                displayName = form.displayName,
                email = form.email,
                status = form.status
            )
        )
    }

    fun setRoles(id: Long, form: UserRoleForm) {
        koki.setUserRoles(
            id,
            SetRoleListRequest(roleIds = form.roleId)
        )
    }
}
