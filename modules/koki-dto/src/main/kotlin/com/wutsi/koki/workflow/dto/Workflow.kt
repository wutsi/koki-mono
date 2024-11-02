package com.wutsi.koki.workflow.dto

import com.wutsi.koki.tenant.dto.Role
import java.util.Date

data class Workflow(
    val id: Long = -1,
    val name: String = "",
    val description: String? = null,
    val active: Boolean = false,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val activities: List<Activity> = emptyList(),
    val roles: List<Role> = emptyList()
)
