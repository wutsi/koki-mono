package com.wutsi.koki.workflow.server.service.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.engine.ActivityRunner
import com.wutsi.koki.workflow.server.engine.WorkflowEngine

abstract class AbstractActivityRunner(
    protected val logger: KVLogger
) : ActivityRunner {
    abstract fun doRun(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine)

    override fun run(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine) {
        logger.add("runner", this::class.simpleName)
        logger.add("activity_instance_id", activityInstance.id)
        logger.add("workflow_instance_id", activityInstance.workflowInstanceId)

        doRun(activityInstance, engine)
    }

    protected fun bindInput(
        activity: ActivityEntity,
        workflowInstance: WorkflowInstanceEntity,
        templateEngine: TemplatingEngine,
        objectMapper: ObjectMapper,
    ): Map<String, String> {
        val state = workflowInstance.stateAsMap(objectMapper)
        val input = activity.inputAsMap(objectMapper)
        return input.map { entry ->
            val xvalue = templateEngine.apply(entry.value.toString(), state)
            entry.key to xvalue
        }.toMap()
    }

    protected fun bindOutput(
        activity: ActivityEntity, result: Map<String, Any>?, objectMapper: ObjectMapper
    ): Map<String, Any> {
        if (result == null) {
            return emptyMap()
        }

        val output = activity.outputAsMap(objectMapper)
        return output.mapNotNull { entry ->
            val value = result[entry.key]
            if (value == null) {
                null
            } else {
                entry.value.toString() to value
            }
        }.toMap()
    }
}
