package com.wutsi.koki.workflow.server.engine.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.script.dto.Language
import com.wutsi.koki.script.server.domain.ScriptEntity
import com.wutsi.koki.script.server.service.ScriptService
import com.wutsi.koki.script.server.service.ScriptingEngine
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.exception.NoScriptException
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.LogService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import com.wutsi.koki.workflow.server.service.runner.ScriptRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

class ScriptRunnerTest {
    private val workflowInstanceService = mock<WorkflowInstanceService>()
    private val activityService = mock<ActivityService>()
    private val scriptService = mock<ScriptService>()
    private val logService = mock<LogService>()
    private val scriptEngine = ScriptingEngine()
    private val templateEngine = MustacheTemplatingEngine(DefaultMustacheFactory())
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val logger = DefaultKVLogger()
    private val engine = mock<WorkflowEngine>()

    private val executor = ScriptRunner(
        objectMapper = objectMapper,
        workflowInstanceService = workflowInstanceService,
        activityService = activityService,
        scriptService = scriptService,
        templateEngine = templateEngine,
        logService = logService,
        logger = logger,
        scriptEngine = scriptEngine,
    )

    private val tenantId = 1L
    private val script = ScriptEntity(
        id = "111",
        name = "SCR-001",
        tenantId = tenantId,
        language = Language.JAVASCRIPT,
        parameters = "input",
        code = """
            function generate_id(type){
              return type + '-1111-0000';
            }

            var id = generate_id(type)
            console.log('ID generated: ' + id);
        """.trimIndent(),
    )
    private val activity = ActivityEntity(
        id = 333L,
        tenantId = tenantId,
        type = ActivityType.SCRIPT,
        scriptId = script.id,
        input = "{\"type\":\"{{type}}\"}",
        output = "{\"id\":\"case_id\"}",
    )
    val workflowInstance = WorkflowInstanceEntity(
        id = "1111",
        tenantId = tenantId,
        state = "{\"type\":\"T1\"}"
    )
    private val activityInstance = ActivityInstanceEntity(
        id = "11111-01",
        tenantId = tenantId,
        workflowInstanceId = workflowInstance.id!!,
        activityId = activity.id!!,
    )

    @BeforeEach
    fun setUp() {
        scriptEngine.init()

        doReturn(script).whenever(scriptService).get(script.id!!, tenantId)

        doReturn(activity).whenever(activityService).get(activity.id!!)

        doReturn(workflowInstance).whenever(workflowInstanceService).get(workflowInstance.id!!, tenantId)
    }

    @Test
    fun run() {
        executor.run(activityInstance, engine)

        val state = argumentCaptor<Map<String, Any>>()
        verify(engine).done(eq(activityInstance.id!!), state.capture(), eq(tenantId))
        assertEquals("T1-1111-0000", state.firstValue["case_id"])
    }

    @Test
    fun `no script`() {
        doReturn(activity.copy(scriptId = null)).whenever(activityService).get(activity.id!!)

        assertThrows<NoScriptException> { executor.run(activityInstance, engine) }
    }
}
