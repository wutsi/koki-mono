package com.wutsi.koki.tenant.dto

data class SearchUserResponse(
    val users: List<User> = emptyList()
)
