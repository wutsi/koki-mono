package com.wutsi.koki.form.dto

import jakarta.validation.constraints.Size

data class UpdateFormDataRequest(
    val data: Map<String, Any> = emptyMap(),
    @get:Size(max = 36) val activityInstanceId: String? = null,
)
