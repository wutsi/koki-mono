package com.wutsi.koki.portal.client.account.service

import com.wutsi.koki.portal.client.account.mapper.AccountMapper
import com.wutsi.koki.portal.client.account.model.AccountModel
import com.wutsi.koki.sdk.KokiAccounts
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val koki: KokiAccounts,
    private val mapper: AccountMapper,
) {
    fun account(id: Long): AccountModel {
        val account = koki.account(id).account

        return mapper.toAccountModel(account)
    }

    fun accounts(ids: List<Long>): List<AccountModel> {
        val accounts = koki.accounts(
            keyword = null,
            ids = ids,
            accountTypeIds = emptyList(),
            managedByIds = emptyList(),
            createdByIds = emptyList(),
            limit = ids.size,
            offset = 0
        ).accounts

        return accounts.map { account -> mapper.toAccountModel(account) }
    }
}
