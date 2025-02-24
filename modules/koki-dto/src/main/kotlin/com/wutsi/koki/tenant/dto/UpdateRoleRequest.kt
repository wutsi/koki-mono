package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UpdateRoleRequest(
    @get:NotEmpty @Size(max = 100) val name: String = "",
    val title: String? = null,
    val active: Boolean = true,
    val description: String? = null,
    val permissionIds: List<Long> = emptyList()
)
