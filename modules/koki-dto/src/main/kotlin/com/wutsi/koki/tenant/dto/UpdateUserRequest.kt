package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UpdateUserRequest(
    @get:Size(max = 50) val displayName: String? = null,
    @get:Email @get:Size(max = 255) val email: String? = null,
    @get:Size(max = 30) val mobile: String? = null,
    @get:Size(max = 2) val language: String? = null,
    @get:Size(max = 50) val employer: String? = null,
    @get:Size(max = 2) val country: String? = null,
    val cityId: Long? = null,
    val categoryId: Long? = null,
    val roleIds: List<Long> = emptyList(),
)
