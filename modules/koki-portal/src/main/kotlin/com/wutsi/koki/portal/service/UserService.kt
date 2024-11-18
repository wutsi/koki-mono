package com.wutsi.koki.portal.rest

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
}
