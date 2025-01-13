package com.wutsi.koki.portal.model

import com.wutsi.koki.portal.user.model.RoleModel
import java.util.Date

data class WorkflowModel(
    val id: Long = -1,
    val name: String = "",
    val title: String = "",
    val description: String? = null,
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

    val titleOrName: String
        get() = if (title.isEmpty()) {
            name
        } else {
            title
        }

    val url: String
        get() = "/settings/workflows/$id"

    val editUrl: String
        get() = "/settings/workflows/$id/edit"

    val startUrl: String
        get() = "/settings/workflows/$id/start"
}
