package com.wutsi.koki.portal.user.service

import com.wutsi.koki.portal.user.mapper.UserMapper
import com.wutsi.koki.portal.user.model.UserForm
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.sdk.KokiUsers
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.dto.UserType
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
        status: UserStatus? = null,
        type: UserType? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<UserModel> {
        val users = koki.users(
            keyword = keyword,
            ids = ids,
            roleIds = roleIds,
            permissions = permissions,
            status = status,
            type = type,
            limit = limit,
            offset = offset
        ).users

        return users.map { user -> mapper.toUserModel(user) }
    }

    fun create(form: UserForm): Long {
        return koki.createUser(
            CreateUserRequest(
                displayName = form.displayName,
                username = form.username,
                email = form.email,
                roleIds = form.roleIds,
                language = form.language,
                password = form.password,
                status = UserStatus.ACTIVE,
                type = UserType.EMPLOYEE,
            )
        ).userId
    }

    fun update(id: Long, form: UserForm) {
        koki.updateUser(
            id,
            UpdateUserRequest(
                displayName = form.displayName,
                username = form.username,
                email = form.email,
                status = form.status,
                roleIds = form.roleIds,
                language = form.language,
            )
        )
    }
}
