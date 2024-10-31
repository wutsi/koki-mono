package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.NotEmpty

data class UpdateUserRequest(
    @NotEmpty val email: String = "",
    @NotEmpty val displayName: String = "",
)
