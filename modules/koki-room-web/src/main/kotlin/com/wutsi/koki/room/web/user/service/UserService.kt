package com.wutsi.koki.room.web.user.service

import com.wutsi.koki.room.web.user.mapper.UserMapper
import com.wutsi.koki.room.web.user.model.UserModel
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
) {
    fun user(id: Long): UserModel {
        val user = koki.user(id).user
        return mapper.toUserModel(user)
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
}
