package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.NotEmpty

data class UpdateUserRequest(
    @get:NotEmpty val email: String = "",
    @get:NotEmpty val displayName: String = "",
)
