package com.wutsi.koki.account.server.mapper

import com.wutsi.koki.account.dto.AccountUser
import com.wutsi.koki.account.server.domain.AccountUserEntity
import org.springframework.stereotype.Service

@Service
class AccountUserMapper {
    fun toAccountUser(entity: AccountUserEntity): AccountUser {
        return AccountUser(
            id = entity.id ?: -1,
            accountId = entity.accountId,
            username = entity.username,
            status = entity.status,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
