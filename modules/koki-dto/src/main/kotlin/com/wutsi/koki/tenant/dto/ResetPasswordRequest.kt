package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.NotEmpty

data class ResetPasswordRequest(
    @get:NotEmpty val tokenId: String = "",
    @get:NotEmpty val password: String = ""
)
