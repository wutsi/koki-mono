package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UpdateUserRequest(
    @get:NotEmpty @get:Size(max = 255) @get:Email val email: String = "",
    @get:NotEmpty val password: String = "",
    @get:NotEmpty val displayName: String = "",
    @get:Size(max = 2) val language: String? = null,
    val status: UserStatus = UserStatus.NEW,
    val roleIds: List<Long> = emptyList(),
)
