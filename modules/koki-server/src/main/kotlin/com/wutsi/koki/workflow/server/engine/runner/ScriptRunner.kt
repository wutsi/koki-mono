package com.wutsi.koki.workflow.server.service.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.script.server.service.ScriptService
import com.wutsi.koki.script.server.service.ScriptingEngine
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
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
        val input = bindInput(activity, workflowInstance, templateEngine, objectMapper)
        val result = scriptEngine.eval(
            code = script.code,
            language = script.language,
            input = input,
            writer = writer,
        )

        val output = bindOutput(activity, result, objectMapper)
        logService.info(
            tenantId = script.tenantId,
            message = "Script ${script.name} executed",
            activityInstanceId = activityInstance.id,
            workflowInstanceId = activityInstance.workflowInstanceId,
            metadata = mapOf(
                "script_id" to (script.id ?: ""),
                "script_name" to script.name,
                "script_input" to input,
                "script_result" to result,
                "output" to output,
                "console" to writer.toString(),
            )
        )
        return output
    }
}
