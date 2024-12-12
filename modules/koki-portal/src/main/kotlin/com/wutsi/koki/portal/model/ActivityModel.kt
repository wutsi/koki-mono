package com.wutsi.koki.portal.model

import com.wutsi.koki.workflow.dto.ActivityType

data class ActivityModel(
    val id: Long = -1,
    val workflowId: Long = -1,
    val name: String = "",
    val title: String = "",
    val description: String = "",
    val type: ActivityType = ActivityType.UNKNOWN,
    val requiresApproval: Boolean = false,
    val role: RoleModel? = null,
    val form: FormModel? = null,
    val message: MessageModel? = null,
    val script: ScriptModel? = null,
    val inputJSON: String? = null,
    val outputJSON: String? = null,
) {
    val longTitle: String
        get() = if (title.isEmpty()) {
            name
        } else {
            "$name - $title"
        }

    val url: String
        get() = "/settings/workflows/$workflowId/activities/$id"

    val requiresUserInput: Boolean
        get() = (type == ActivityType.USER) ||
            (type == ActivityType.MANUAL)
}
