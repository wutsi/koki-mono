package com.wutsi.koki.portal.model

import java.util.Date

data class WorkflowModel(
    val id: Long = -1,
    val name: String = "",
    val title: String = "",
    val description: String = "",
    val active: Boolean = true,
    val requiresApprover: Boolean = false,
    val approverRole: RoleModel? = null,
    val imageUrl: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val activities: List<ActivityModel> = emptyList(),
    val roles: List<RoleModel> = emptyList(),
    val parameters: List<String> = emptyList(),
    val workflowInstanceCount: Long = 0,
    val createdAtText: String = "",
    val modifiedAtText: String = "",
) {
    val longTitle: String
        get() = if (title.isEmpty()) {
            name
        } else {
            "$name - $title"
        }

    val url: String
        get() = "/workflows/$id"

    val updateUrl: String
        get() = "/workflows/$id/update"

    val startUrl: String
        get() = "/workflows/$id/start"
}
