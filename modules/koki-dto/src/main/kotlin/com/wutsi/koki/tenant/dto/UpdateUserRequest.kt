package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpdateUserRequest(
    @get:NotEmpty val email: String = "",
    @get:NotEmpty val displayName: String = "",
    @get:NotNull val status: UserStatus? = null,
    @get:Size(max = 2) val language: String? = null,

    val roleIds: List<Long> = emptyList(),
)
