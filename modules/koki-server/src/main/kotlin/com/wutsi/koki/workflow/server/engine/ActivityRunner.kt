package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity

interface ActivityWorker {
    fun execute(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine)
}
