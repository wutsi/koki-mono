package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity

interface ActivityExecutor {
    fun execute(activity: ActivityInstanceEntity)
}
