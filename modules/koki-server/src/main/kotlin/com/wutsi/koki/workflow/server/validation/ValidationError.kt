package com.wutsi.koki.workflow.server.io.validation

data class ValidationError(
    val location: String,
    val message: String,
)
