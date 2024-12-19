package com.wutsi.koki.workflow.server.engine.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.service.dto.AuthorizationType
import com.wutsi.koki.service.server.domain.ServiceEntity
import com.wutsi.koki.service.server.service.ServiceCaller
import com.wutsi.koki.service.server.service.ServiceResponse
import com.wutsi.koki.service.server.service.ServiceService
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.LogService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import kotlin.test.assertEquals

class ServiceRunnerTest {
    private val workflowInstanceService = mock<WorkflowInstanceService>()
    private val activityService = mock<ActivityService>()
    private val serviceService = mock<ServiceService>()
    private val logService = mock<LogService>()
    private val serviceCaller = mock<ServiceCaller>()
    private val templateEngine = MustacheTemplatingEngine(DefaultMustacheFactory())
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val logger = DefaultKVLogger()
    private val engine = mock<WorkflowEngine>()

    private val executor = ServiceRunner(
        objectMapper = objectMapper,
        workflowInstanceService = workflowInstanceService,
        activityService = activityService,
        serviceService = serviceService,
        templateEngine = templateEngine,
        logService = logService,
        logger = logger,
        caller = serviceCaller,
    )

    private val tenantId = 1L
    private val service = ServiceEntity(
        id = "111",
        name = "SVR-001",
        tenantId = tenantId,
        baseUrl = "https://localhost:7555",
        authorizationType = AuthorizationType.API_KEY,
        apiKey = "foo",
    )
    private val activity = ActivityEntity(
        id = 333L,
        tenantId = tenantId,
        type = ActivityType.SCRIPT,
        serviceId = service.id,
        input = "{\"type\":\"{{type}}\",\"email\":\"{{employee_email}}\"}",
        output = "{\"id\":\"case_id\"}",
        method = "POST",
        path = "/v1/process"
    )
    val workflowInstance = WorkflowInstanceEntity(
        id = "1111",
        tenantId = tenantId,
        state = "{\"type\":\"T1\",\"employee_email\":\"ray.sponsible@gmail.com\",\"employee_name\":\"Ray Sponsible\"}"
    )
    private val activityInstance = ActivityInstanceEntity(
        id = "11111-01",
        tenantId = tenantId,
        workflowInstanceId = workflowInstance.id!!,
        activityId = activity.id!!,
    )

    @BeforeEach
    fun setUp() {
        doReturn(service).whenever(serviceService).get(service.id!!, tenantId)

        doReturn(activity).whenever(activityService).get(activity.id!!)

        doReturn(workflowInstance).whenever(workflowInstanceService).get(workflowInstance.id!!, tenantId)
    }

    @Test
    fun run() {
        doReturn(
            ServiceResponse(
                body = mapOf(
                    "id" to "11111",
                    "amount" to 15000
                ),
                statusCode = HttpStatusCode.valueOf(200),
            )
        ).whenever(serviceCaller).call(any(), any(), any(), any(), anyOrNull())

        executor.run(activityInstance, engine)

        verify(serviceCaller).call(
            service,
            HttpMethod.POST,
            activity.path,
            mapOf(
                "type" to "T1",
                "email" to "ray.sponsible@gmail.com",
            ),
            workflowInstance.id
        )

        val state = argumentCaptor<Map<String, Any>>()
        verify(engine).done(eq(activityInstance.id!!), state.capture(), eq(tenantId))
        assertEquals(mapOf("case_id" to "11111"), state.firstValue)
    }
}
