package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity

interface ActivityExecutor {
    fun execute(activity: ActivityInstanceEntity, engine: WorkflowEngine)
}
