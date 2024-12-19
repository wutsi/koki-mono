package com.wutsi.koki.workflow.server.engine.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.service.server.service.ServiceCaller
import com.wutsi.koki.service.server.service.ServiceService
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.exception.NoServiceException
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.LogService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import com.wutsi.koki.workflow.server.service.runner.AbstractActivityRunner
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service

@Service
class ServiceRunner(
    private val workflowInstanceService: WorkflowInstanceService,
    private val activityService: ActivityService,
    private val serviceService: ServiceService,
    private val templateEngine: TemplatingEngine,
    private val objectMapper: ObjectMapper,
    private val logService: LogService,
    private val caller: ServiceCaller,
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
        val state = workflowInstance.stateAsMap(objectMapper)

        val path = buildPath(activity, state)
        logger.add("service_path", path)

        val input = bindInput(activity, workflowInstance, templateEngine, objectMapper)
        val response = caller.call(
            service = service,
            method = HttpMethod.valueOf(activity.method!!.uppercase()),
            path = path,
            workflowInstanceId = workflowInstance.id!!,
            input = input,
        )

        val output = bindOutput(activity, response.body, objectMapper)
        logService.info(
            tenantId = service.tenantId,
            message = "Script ${service.name} called",
            activityInstanceId = activityInstance.id,
            workflowInstanceId = activityInstance.workflowInstanceId,
            metadata = mapOf(
                "service_id" to (service.id ?: ""),
                "service_name" to service.name,
                "service_request" to input,
                "service_response_body" to (response.body ?: emptyMap()),
                "service_response_status" to response.statusCode.value(),
                "output" to output,
            )
        )
        return output
    }

    private fun buildPath(activity: ActivityEntity, state: Map<String, Any>): String? {
        return activity.path?.let { path ->
            templateEngine.apply(path, state)
        }
    }
}
