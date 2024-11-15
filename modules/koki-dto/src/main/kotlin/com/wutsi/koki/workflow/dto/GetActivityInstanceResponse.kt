package com.wutsi.koki.workflow.dto

import com.wutsi.koki.workflow.server.domain.ActivityInstance

data class GetActivityInstanceResponse(
    val activityInstance: ActivityInstance = ActivityInstance()
)
