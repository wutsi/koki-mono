package com.wutsi.koki.portal.client.account.service

import com.wutsi.koki.account.dto.CreateAccountUserRequest
import com.wutsi.koki.portal.client.account.form.InvitationForm
import com.wutsi.koki.portal.client.account.mapper.AccountMapper
import com.wutsi.koki.portal.client.account.model.AccountUserModel
import com.wutsi.koki.sdk.KokiAccounts
import com.wutsi.koki.tenant.dto.UserStatus
import org.springframework.stereotype.Service

@Service
class UserService(
    private val koki: KokiAccounts,
    private val mapper: AccountMapper
) {
    fun user(id: Long): AccountUserModel {
        val user = koki.user(id).accountUser
        val account = koki.account(user.accountId).account
        return mapper.toAccountUserModel(user, account)
    }

    fun create(form: InvitationForm): Long {
        return koki.createUser(
            CreateAccountUserRequest(
                username = form.username,
                password = form.password,
                accountId = form.accountId,
                status = UserStatus.ACTIVE,
            )
        ).accountUserId
    }
}
