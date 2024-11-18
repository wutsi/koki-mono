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
    val activities: List<ActivityModel> = emptyList(),
    val roles: List<RoleModel> = emptyList(),
    val parameters: List<String> = emptyList(),
) {
    val longTitle: String
        get() = if (title.isEmpty()) {
            name
        } else {
            "$name - $title"
        }

    val url: String
        get() = "/workflows/$id"

    val editUrl: String
        get() = "/workflows/$id/edit"
}
