package com.wutsi.koki.account.server.mapper

import com.wutsi.koki.account.dto.AccountType
import com.wutsi.koki.account.dto.AccountTypeSummary
import com.wutsi.koki.tenant.server.domain.AccountTypeEntity
import org.springframework.stereotype.Service

@Service
class AccountTypeMapper {
    fun toAccountType(entity: AccountTypeEntity): AccountType {
        return AccountType(
            id = entity.id!!,
            name = entity.name,
            title = entity.title,
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
    fun toAccountTypeSummary(entity: AccountTypeEntity): AccountTypeSummary {
        return AccountTypeSummary(
            id = entity.id!!,
            name = entity.name,
            title = entity.title,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
