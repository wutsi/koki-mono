package com.wutsi.koki.form.event

data class ApprovalStartedEvent(
    val tenantId: Long = -1,
    val activityInstanceId: String = "",
)
