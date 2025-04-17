package com.wutsi.koki.security.dto

import jakarta.validation.constraints.NotEmpty

data class LoginRequest(
    @get:NotEmpty val username: String = "",
    @get:NotEmpty val application: String = "",
    @get:NotEmpty val password: String = "",
)
