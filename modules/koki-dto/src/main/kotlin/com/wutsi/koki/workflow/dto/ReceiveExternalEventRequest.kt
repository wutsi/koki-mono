package com.wutsi.koki.workflow.dto

import jakarta.validation.constraints.NotEmpty

data class ReceiveExternalEventRequest(
    @get:NotEmpty val name: String = "",
    val data: Map<String, Any> = emptyMap(),
)
