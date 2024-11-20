package com.wutsi.koki.portal.service

import com.wutsi.koki.portal.mapper.UserMapper
import com.wutsi.koki.portal.model.UserModel
import com.wutsi.koki.sdk.KokiUser
import org.springframework.stereotype.Service

@Service
class UserService(
    private val mapper: UserMapper,
    private val kokiUser: KokiUser
) {
    fun get(id: Long): UserModel {
        return mapper.toUserModel(kokiUser.user(id).user)
    }

    fun search(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        roleIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): List<UserModel> {
        val users = kokiUser.users(
            keyword = keyword,
            ids = ids,
            roleIds = roleIds,
            limit = limit,
            offset = offset
        ).users

        return users.map { user -> mapper.toUserModel(user) }
    }
}
