package com.wutsi.koki.workflow.dto

data class CompleteActivityInstanceRequest(
    val state: Map<String, String> = emptyMap()
)