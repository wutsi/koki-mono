package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.NotEmpty

data class CreateUserRequest(
    @get:NotEmpty val email: String = "",
    @get:NotEmpty val password: String = "",
    @get:NotEmpty val displayName: String = "",
    val roleIds: List<Long> = emptyList(),
)
