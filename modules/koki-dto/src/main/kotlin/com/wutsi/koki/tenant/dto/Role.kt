package com.wutsi.koki.tenant.dto

import java.util.Date

data class Role(
    val id: Long = -1,
    val name: String = "",
    val title: String? = null,
    val active: Boolean = true,
    val description: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdById: Long? = null,
    val modifiedById: Long? = null,
    val permissionIds: List<Long> = emptyList()
)
