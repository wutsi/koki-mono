package com.wutsi.koki.workflow.server.engine.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.service.server.service.ServiceService
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.exception.NoServiceException
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.LogService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import com.wutsi.koki.workflow.server.service.runner.AbstractActivityRunner

class ServiceRunner(
    private val workflowInstanceService: WorkflowInstanceService,
    private val activityService: ActivityService,
    private val serviceService: ServiceService,
    private val templateEngine: TemplatingEngine,
    private val objectMapper: ObjectMapper,
    private val logService: LogService,
    logger: KVLogger
) : AbstractActivityRunner(logger) {
    override fun doRun(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine) {
        val state = call(activityInstance)
        engine.done(activityInstance.id!!, state, activityInstance.tenantId)
    }

    private fun call(activityInstance: ActivityInstanceEntity): Map<String, Any> {
        val activity = activityService.get(activityInstance.activityId)
        val service = activity.serviceId?.let { id -> serviceService.get(id, activityInstance.tenantId) }
            ?: throw NoServiceException("Service not setup")
        logger.add("service_name", service.name)

        val workflowInstance = workflowInstanceService.get(
            activityInstance.workflowInstanceId,
            activityInstance.tenantId
        )
    }
}
