package com.wutsi.koki.file.dto

import jakarta.validation.constraints.NotEmpty

data class CreateFileRequest(
    val workflowInstanceId: String? = null,
    val formId: String? = null,
    @get:NotEmpty val url: String = "",
    @get:NotEmpty val name: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
)
