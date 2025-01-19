package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class UpdateUserRequest(
    @get:NotEmpty val email: String = "",
    @get:NotEmpty val displayName: String = "",
    @get:NotNull val status: UserStatus? = null,
)
