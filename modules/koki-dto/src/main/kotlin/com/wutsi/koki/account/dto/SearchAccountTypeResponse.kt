package com.wutsi.koki.account.dto

data class SearchAccountTypeResponse(
    val accountTypes: List<AccountTypeSummary> = emptyList()
)
