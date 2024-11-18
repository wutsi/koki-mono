package com.wutsi.koki.portal.model

import java.util.Date

data class WorkflowModel(
    val id: Long = -1,
    val name: String = "",
    val title: String = "",
    val description: String = "",
    val active: Boolean = true,
    val requiresApprover: Boolean = false,
    val imageUrl: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val activities: List<ActivityModel> = emptyList()
) {
    val url: String
        get() = "/workflows/$id"
}
