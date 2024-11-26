package com.wutsi.koki.workflow.server.service.runner

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.ActivityRunner
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EndRunner(
    private val workflowInstanceService: WorkflowInstanceService
) : ActivityRunner {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EndRunner::class.java)
    }

    override fun run(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> ${activityInstance.workflowInstanceId} > ${activityInstance.id} executing")
        }
        engine.done(activityInstance, emptyMap())

        val workflowInstance = workflowInstanceService.get(
            activityInstance.workflowInstanceId,
            activityInstance.tenantId
        )
        engine.done(workflowInstance)
    }
}
