package com.wutsi.koki.account.dto

data class SearchAccountResponse(
    val accounts: List<AccountSummary> = emptyList()
)
