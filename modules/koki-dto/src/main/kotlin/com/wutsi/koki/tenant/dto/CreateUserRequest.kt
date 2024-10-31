package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.NotEmpty

data class CreateUserRequest(
    @NotEmpty val email: String = "",
    @NotEmpty val password: String = "",
    @NotEmpty val displayName: String = "",
)
