package com.wutsi.koki.room.web.account.service

import com.wutsi.koki.room.web.account.mapper.AccountMapper
import com.wutsi.koki.room.web.account.model.AccountModel
import com.wutsi.koki.sdk.KokiAccounts
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val koki: KokiAccounts,
    private val mapper: AccountMapper,
) {
    fun account(id: Long): AccountModel {
        val account = koki.account(id).account
        return mapper.toAccountModel(entity = account)
    }

    fun accounts(
        ids: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<AccountModel> {
        val accounts = koki.accounts(
            ids = ids,
            keyword = null,
            accountTypeIds = emptyList(),
            managedByIds = emptyList(),
            createdByIds = emptyList(),
            limit = limit,
            offset = offset
        ).accounts

        return accounts.map { account -> mapper.toAccountModel(entity = account) }
    }
}
