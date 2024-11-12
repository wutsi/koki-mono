package com.wutsi.koki.party.dto

import jakarta.validation.constraints.NotEmpty

data class LoginRequest(
    @get:NotEmpty val email: String = "",
    val password: String = ""
)
