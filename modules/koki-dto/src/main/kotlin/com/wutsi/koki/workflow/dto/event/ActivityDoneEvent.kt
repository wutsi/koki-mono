package com.wutsi.koki.form.event

data class ActivityDoneEvent(
    val tenantId: Long = -1,
    val activityInstanceId: String = "",
)
