package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.NotEmpty

data class CreateRoleRequest(
    @get:NotEmpty val name: String = "",
    @get:NotEmpty val title: String? = null,
    val active: Boolean = true,
    val description: String? = null,
)
