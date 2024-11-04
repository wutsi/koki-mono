package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity

interface WorkflowEngine {
    fun execute(activity: ActivityInstanceEntity): Boolean

    fun done(activity: ActivityInstanceEntity)
}
