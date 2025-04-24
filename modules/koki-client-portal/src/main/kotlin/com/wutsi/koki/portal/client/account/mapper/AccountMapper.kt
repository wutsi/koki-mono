package com.wutsi.koki.portal.client.account.mapper

import com.wutsi.koki.account.dto.Account
import com.wutsi.koki.account.dto.AccountSummary
import com.wutsi.koki.account.dto.AccountUser
import com.wutsi.koki.account.dto.Invitation
import com.wutsi.koki.portal.client.account.model.AccountModel
import com.wutsi.koki.portal.client.account.model.AccountUserModel
import com.wutsi.koki.portal.client.account.model.InvitationModel
import com.wutsi.koki.portal.client.common.mapper.TenantAwareMapper
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class AccountMapper : TenantAwareMapper() {
    fun toInvitationModel(entity: Invitation, account: Account): InvitationModel {
        val fmt = createDateTimeFormat()
        return InvitationModel(
            id = entity.id,
            account = toAccountModel(account),
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
        )
    }

    fun toAccountUserModel(entity: AccountUser, account: Account): AccountUserModel {
        val fmt = createDateTimeFormat()
        return AccountUserModel(
            id = entity.id,
            account = toAccountModel(account),
            username = entity.username,
            status = entity.status,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

    fun toAccountModel(entity: Account): AccountModel {
        val fmt = createDateTimeFormat()
        return AccountModel(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            language = entity.language,
            languageText = entity.language?.let { lang -> Locale(lang).displayName },
            accountUserId = entity.accountUserId,
            invitationId = entity.invitationId,
        )
    }

    fun toAccountModel(entity: AccountSummary): AccountModel {
        val fmt = createDateTimeFormat()
        return AccountModel(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
        )
    }
}
