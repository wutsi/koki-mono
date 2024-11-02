package com.wutsi.koki.workflow.server.validation

data class ValidationError(
    val location: String,
    val message: String,
)
