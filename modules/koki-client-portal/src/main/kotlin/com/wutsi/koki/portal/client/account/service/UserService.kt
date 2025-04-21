package com.wutsi.koki.portal.client.account.service

import com.wutsi.koki.account.dto.CreateAccountUserRequest
import com.wutsi.koki.account.dto.CreateInvitationRequest
import com.wutsi.koki.portal.client.account.form.InvitationForm
import com.wutsi.koki.portal.client.account.mapper.AccountMapper
import com.wutsi.koki.portal.client.account.model.InvitationModel
import com.wutsi.koki.sdk.KokiAccounts
import org.springframework.stereotype.Service

@Service
class InvitationService(
    private val koki: KokiAccounts,
    private val mapper: AccountMapper
) {
    fun invitation(id: String): InvitationModel {
        val invitation = koki.invitation(id).invitation
        val account = koki.account(invitation.accountId).account
        return mapper.toInvitationModel(
            entity = invitation,
            account = account,
        )
    }

    fun createUser(form: InvitationForm): Long {
        return koki.createUser(
            CreateAccountUserRequest(
                username = form.username,
                password = form.password,
                accountId = form.accountId
            )
        ).accountUserId
    }
}
