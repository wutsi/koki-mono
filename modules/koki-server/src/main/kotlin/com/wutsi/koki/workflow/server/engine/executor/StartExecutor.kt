package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.ActivityExecutor
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import org.springframework.stereotype.Service

@Service
class StartExecutor : ActivityExecutor {
    override fun execute(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine) {
        engine.done(activityInstance)
    }
}
