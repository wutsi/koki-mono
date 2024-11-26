package com.wutsi.koki.workflow.server.service.runner

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.ActivityRunner
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StartRunner : ActivityRunner {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StartRunner::class.java)
    }

    override fun run(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> ${activityInstance.workflowInstanceId} > ${activityInstance.id} executing")
        }
        engine.done(activityInstance, emptyMap())
    }
}
