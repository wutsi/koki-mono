package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import org.springframework.stereotype.Service

@Service
class WorkflowEngineImpl : WorkflowEngine {
    override fun execute(activity: ActivityInstanceEntity): Boolean {
        return true
    }

    override fun done(activity: ActivityInstanceEntity) {
    }
}
