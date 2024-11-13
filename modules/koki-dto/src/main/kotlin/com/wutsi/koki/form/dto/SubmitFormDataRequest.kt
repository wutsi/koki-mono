package com.wutsi.koki.form.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class SubmitFormDataRequest(
    @get:NotEmpty() val formId: String = "",
    @get:Size(max = 36) val workflowInstanceId: String? = null,
    @get:Size(max = 36) val activityInstanceId: String? = null,
    val data: Map<String, Any> = emptyMap(),
)
