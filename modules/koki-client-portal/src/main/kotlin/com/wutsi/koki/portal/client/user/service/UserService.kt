package com.wutsi.koki.portal.client.user.service

import com.wutsi.koki.portal.client.user.mapper.UserMapper
import com.wutsi.koki.portal.client.user.model.UserModel
import com.wutsi.koki.sdk.KokiAccounts
import com.wutsi.koki.sdk.KokiUsers
import org.springframework.stereotype.Service

@Service
class UserService(
    private val koki: KokiUsers,
    private val kokiAccount: KokiAccounts,
    private val mapper: UserMapper,
) {
    fun user(id: Long): UserModel {
        val user = koki.user(id).user
        val account = kokiAccount.accounts(
            userIds = listOf(id),
            limit = 1,
            offset = 0,
            accountTypeIds = emptyList(),
            keyword = null,
            ids = emptyList(),
            createdByIds = emptyList(),
            managedByIds = emptyList(),
        ).accounts.firstOrNull()
        return mapper.toUserModel(user, account?.id ?: -1)
    }
}
