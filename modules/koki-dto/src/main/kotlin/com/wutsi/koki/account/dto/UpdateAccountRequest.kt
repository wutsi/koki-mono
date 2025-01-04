package com.wutsi.koki.account.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class UpdateAccountRequest(
    @get:NotEmpty val name: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    @get:Email val email: String? = null,
    val website: String? = null,
    val language: String? = null,
    val description: String? = null,
    val attributes: Map<Long, String> = emptyMap(),
    val managedById: Long? = null,
)
