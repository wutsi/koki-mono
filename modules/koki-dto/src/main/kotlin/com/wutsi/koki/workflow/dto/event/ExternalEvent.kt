package com.wutsi.koki.form.event

data class ExternalEvent(
    val tenantId: Long = -1,
    val workflowInstanceId: String = "",
    val name: String = "",
    val data: Map<String, Any> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis(),
)
