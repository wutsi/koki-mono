package com.wutsi.koki.tenant.dto

data class SearchUserResponse(
    val users: List<UserSummary> = emptyList()
)
