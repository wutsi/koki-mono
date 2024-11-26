package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity

interface ActivityRunner {
    fun run(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine)
}
