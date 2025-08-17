package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class SendPasswordRequest(
    @get:Email @get:NotEmpty val email: String = ""
)
