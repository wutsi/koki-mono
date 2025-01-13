package com.wutsi.koki.portal.model

import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class FormSubmissionModel(
    val id: String = "",
    val form: FormModel = FormModel(),
    val data: Map<String, Any> = emptyMap(),
    val dataJSON: String = "",
    val workflowInstanceId: String? = null,
    val activityInstanceId: String? = null,
    val submittedAt: Date = Date(),
    val submittedAtText: String = "",
    val submittedBy: UserModel? = null,
) {
    val url: String
        get() = "/settings/forms/submissions/$id"
}
