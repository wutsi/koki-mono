package com.wutsi.koki.workflow.server.service.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.script.server.engine.ScriptingEngine
import com.wutsi.koki.script.server.service.ScriptService
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.exception.NoScriptException
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.LogService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.springframework.stereotype.Service
import java.io.StringWriter

@Service
class ScriptRunner(
    private val workflowInstanceService: WorkflowInstanceService,
    private val activityService: ActivityService,
    private val scriptService: ScriptService,
    private val templateEngine: TemplatingEngine,
    private val scriptEngine: ScriptingEngine,
    private val objectMapper: ObjectMapper,
    private val logService: LogService,
    logger: KVLogger
) : AbstractActivityRunner(logger) {
    override fun doRun(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine) {
        val state = eval(activityInstance)
        engine.done(activityInstance.id!!, state, activityInstance.tenantId)
    }

    private fun eval(activityInstance: ActivityInstanceEntity): Map<String, Any> {
        val activity = activityService.get(activityInstance.activityId)
        val script = activity.scriptId?.let { id -> scriptService.get(id, activityInstance.tenantId) }
            ?: throw NoScriptException("Script not setup")
        logger.add("script_name", script.name)

        val workflowInstance = workflowInstanceService.get(
            activityInstance.workflowInstanceId,
            activityInstance.tenantId
        )
        val writer = StringWriter()
        val inputs = prepareInput(activity, workflowInstance)
        inputs.forEach { entry -> logger.add("script_input_${entry.key}", entry.value) }

        val output = activity.outputAsMap(objectMapper)
        val result = scriptEngine.eval(
            code = script.code,
            language = script.language,
            inputs = inputs,
            writer = writer,
        )
        result.forEach { entry -> logger.add("script_result_${entry.key}", entry.value) }

        val state = output.mapNotNull { entry ->
            val value = result[entry.key]
            if (value == null) {
                null
            } else {
                entry.value.toString() to value
            }
        }.toMap()
        state.forEach { entry -> logger.add("state_${entry.key}", entry.value) }

        logService.info(
            tenantId = script.tenantId,
            message = "Script ${script.name} executed",
            activityInstanceId = activityInstance.id,
            workflowInstanceId = activityInstance.workflowInstanceId,
            metadata = mapOf(
                "script_id" to (script.id ?: ""),
                "script_name" to script.name,
                "input" to inputs,
                "output" to result,
                "console" to writer.toString(),
            )
        )
        return state
    }

    private fun prepareInput(activity: ActivityEntity, workflowInstance: WorkflowInstanceEntity): Map<String, String> {
        val state = workflowInstance.stateAsMap(objectMapper)
        val input = activity.inputAsMap(objectMapper)
        return input.map { entry ->
            val xvalue = templateEngine.apply(entry.value.toString(), state)
            entry.key to xvalue
        }.toMap()
    }
}
