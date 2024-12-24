package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.error.dto.ParameterType
import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.FlowData
import com.wutsi.koki.workflow.dto.ImportWorkflowRequest
import com.wutsi.koki.workflow.dto.ImportWorkflowResponse
import com.wutsi.koki.workflow.dto.RecipientData
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.dao.ActivityRepository
import com.wutsi.koki.workflow.server.dao.FlowRepository
import com.wutsi.koki.workflow.server.dao.WorkflowRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/ImportWorkflowEndpoint.sql"])
class ImportWorkflowEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var workflowDao: WorkflowRepository

    @Autowired
    private lateinit var activityDao: ActivityRepository

    @Autowired
    private lateinit var flowDao: FlowRepository

    private val request = ImportWorkflowRequest(
        workflow = WorkflowData(
            name = "new",
            description = "This is a new workflow",
            parameters = listOf("PARAM_1 ", "PARAM_2"),
            approverRole = "accountant",
            activities = listOf(
                ActivityData(name = "START", type = ActivityType.START),
                ActivityData(
                    name = "INVOICE",
                    title = "Invoicing...",
                    description = "SAGE create an invoice",
                    type = ActivityType.SERVICE,
                    input = mapOf("foo" to "bar", "a" to "b"),
                    output = mapOf("x" to "y"),
                    requiresApproval = true,
                    role = "accountant",
                    form = "f-100",
                    message = "m-100",
                    script = "s-100",
                    service = "srv-100",
                    event = "order-received",
                    path = "/activities",
                    method = "POST",
                    recipient = RecipientData(
                        email = "ray.sponsible@gmail.com",
                        displayName = "Ray Sponsible",
                    )
                ),
                ActivityData(
                    name = "STOP",
                    type = ActivityType.END,
                ),
            ),
            flows = listOf(
                FlowData(from = "START", to = "INVOICE"),
                FlowData(from = "INVOICE", to = "STOP", expression = "A==true"),
            )
        )
    )

    @Test
    fun create() {
        val result = rest.postForEntity("/v1/workflows", request, ImportWorkflowResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflowId = result.body!!.workflowId

        val workflow = workflowDao.findById(workflowId).get()
        assertEquals(request.workflow.name, workflow.name)
        assertEquals(request.workflow.description, workflow.description)
        assertTrue(workflow.active)
        assertEquals(10L, workflow.approverRoleId)
        assertEquals("PARAM_1,PARAM_2", workflow.parameters)

        val activities = activityDao.findByWorkflowId(workflow.id!!)
        assertEquals(3, activities.size)

        val aStart = activityDao.findByNameAndWorkflowId("START", workflow.id)
        assertEquals("START", aStart?.name)
        assertNull(aStart?.title)
        assertEquals(request.workflow.activities[0].type, aStart?.type)
        assertEquals(true, aStart?.active)
        assertNull(aStart?.input)
        assertNull(aStart?.output)
        assertNull(aStart?.roleId)
        assertNull(aStart?.formId)
        assertNull(aStart?.messageId)
        assertNull(aStart?.scriptId)
        assertNull(aStart?.serviceId)
        assertNull(aStart?.path)
        assertNull(aStart?.method)
        assertNull(aStart?.event)
        assertNull(aStart?.recipientDisplayName)
        assertNull(aStart?.recipientEmail)

        val aInvoice = activityDao.findByNameAndWorkflowId("INVOICE", workflow.id)
        assertEquals("INVOICE", aInvoice?.name)
        assertEquals(request.workflow.activities[1].title, aInvoice?.title)
        assertEquals(request.workflow.activities[1].description, aInvoice?.description)
        assertEquals(request.workflow.activities[1].type, aInvoice?.type)
        assertEquals("{\"a\": \"b\", \"foo\": \"bar\"}", aInvoice?.input)
        assertEquals("{\"x\": \"y\"}", aInvoice?.output)
        assertEquals(true, aInvoice?.active)
        assertEquals(request.workflow.activities[1].requiresApproval, aInvoice?.requiresApproval)
        assertEquals(10L, aInvoice?.roleId)
        assertEquals("100", aInvoice?.formId)
        assertEquals("100", aInvoice?.messageId)
        assertEquals("100", aInvoice?.scriptId)
        assertEquals("order-received", aInvoice?.event)
        assertEquals("100", aInvoice?.serviceId)
        assertEquals("/activities", aInvoice?.path)
        assertEquals("POST", aInvoice?.method)
        assertEquals("Ray Sponsible", aInvoice?.recipientDisplayName)
        assertEquals("ray.sponsible@gmail.com", aInvoice?.recipientEmail)

        val aEnd = activityDao.findByNameAndWorkflowId("STOP", workflow.id)
        assertEquals("STOP", aEnd?.name)
        assertNull(aEnd?.title)
        assertEquals(request.workflow.activities[2].type, aEnd?.type)
        assertEquals(true, aEnd?.active)
        assertNull(aEnd?.roleId)
        assertNull(aEnd?.formId)
        assertNull(aEnd?.messageId)
        assertNull(aEnd?.scriptId)
        assertNull(aEnd?.input)
        assertNull(aEnd?.output)
        assertNull(aEnd?.event)
        assertNull(aEnd?.serviceId)
        assertNull(aEnd?.path)
        assertNull(aEnd?.method)
        assertNull(aEnd?.recipientDisplayName)
        assertNull(aEnd?.recipientEmail)

        val flows = flowDao.findByWorkflowId(workflow.id!!)
        assertEquals(2, flows.size)

        assertEquals(aStart?.id, flows[0].from.id)
        assertEquals(aInvoice?.id, flows[0].to.id)
        assertEquals(request.workflow.flows[0].expression, flows[0].expression)

        assertEquals(aInvoice?.id, flows[1].from.id)
        assertEquals(aEnd?.id, flows[1].to.id)
        assertEquals(request.workflow.flows[1].expression, flows[1].expression)
    }

    @Test
    fun update() {
        val result = rest.postForEntity("/v1/workflows/100", request, ImportWorkflowResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflowId = result.body!!.workflowId

        val workflow = workflowDao.findById(workflowId).get()
        assertEquals(request.workflow.name, workflow.name)
        assertEquals(request.workflow.description, workflow.description)
        assertTrue(workflow.active)
        assertEquals("PARAM_1,PARAM_2", workflow.parameters)

        val activities = activityDao.findByWorkflowId(workflow.id!!)
        assertEquals(4, activities.size)

        val aStart = activityDao.findByNameAndWorkflowId("START", workflow.id)
        assertEquals("START", aStart?.name)
        assertEquals(request.workflow.activities[0].type, aStart?.type)
        assertEquals(true, aStart?.active)
        assertNull(aStart?.roleId)
        assertNull(aStart?.formId)
        assertNull(aStart?.messageId)
        assertNull(aStart?.scriptId)
        assertNull(aStart?.input)
        assertNull(aStart?.output)
        assertNull(aStart?.event)
        assertNull(aStart?.path)
        assertNull(aStart?.method)
        assertNull(aStart?.event)
        assertNull(aStart?.recipientDisplayName)
        assertNull(aStart?.recipientEmail)

        val aInvoice = activityDao.findByNameAndWorkflowId("INVOICE", workflow.id)
        assertEquals("INVOICE", aInvoice?.name)
        assertEquals(request.workflow.activities[1].description, aInvoice?.description)
        assertEquals(request.workflow.activities[1].type, aInvoice?.type)
        assertEquals("{\"a\": \"b\", \"foo\": \"bar\"}", aInvoice?.input)
        assertEquals("{\"x\": \"y\"}", aInvoice?.output)
        assertEquals(true, aInvoice?.active)
        assertEquals(request.workflow.activities[1].requiresApproval, aInvoice?.requiresApproval)
        assertEquals(10L, aInvoice?.roleId)
        assertEquals("100", aInvoice?.formId)
        assertEquals("100", aInvoice?.messageId)
        assertEquals("100", aInvoice?.scriptId)
        assertEquals("order-received", aInvoice?.event)
        assertEquals("100", aInvoice?.serviceId)
        assertEquals("/activities", aInvoice?.path)
        assertEquals("POST", aInvoice?.method)
        assertEquals("Ray Sponsible", aInvoice?.recipientDisplayName)
        assertEquals("ray.sponsible@gmail.com", aInvoice?.recipientEmail)

        val aEnd = activityDao.findByNameAndWorkflowId("STOP", workflow.id!!)
        assertEquals("STOP", aEnd?.name)
        assertEquals(request.workflow.activities[2].type, aEnd?.type)
        assertEquals(true, aEnd?.active)
        assertNull(aEnd?.roleId)
        assertNull(aEnd?.formId)
        assertNull(aEnd?.messageId)
        assertNull(aEnd?.scriptId)
        assertNull(aEnd?.input)
        assertNull(aEnd?.output)
        assertNull(aEnd?.event)
        assertNull(aEnd?.serviceId)
        assertNull(aEnd?.path)
        assertNull(aEnd?.method)
        assertNull(aEnd?.recipientDisplayName)
        assertNull(aEnd?.recipientEmail)

        val aDeactivated = activityDao.findById(111L).get()
        assertEquals(false, aDeactivated.active)
        assertEquals(workflow.id, aDeactivated.workflowId)
        assertEquals(11L, aDeactivated.roleId)

        val flows = flowDao.findByWorkflowId(workflow.id!!).sortedBy { flow -> flow.from.id }
        assertEquals(2, flows.size)

        assertEquals(aStart?.id, flows[0].from.id)
        assertEquals(aInvoice?.id, flows[0].to.id)
        assertEquals(request.workflow.flows[0].expression, flows[0].expression)

        assertEquals(aInvoice?.id, flows[1].from.id)
        assertEquals(aEnd?.id, flows[1].to.id)
        assertEquals(request.workflow.flows[1].expression, flows[1].expression)
    }

    @Test
    fun `update workflow not found`() {
        val result = rest.postForEntity("/v1/workflows/999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `update workflow of another tenant`() {
        val result = rest.postForEntity("/v1/workflows/200", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `update workflow of non numeric id`() {
        val result = rest.postForEntity("/v1/workflows/xxx", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.HTTP_INVALID_PARAMETER, result.body?.error?.code)
        assertEquals("id", result.body?.error?.parameter?.name)
    }

    @Test
    fun `missing tenant-id header`() {
        super.ignoreTenantIdHeader = true
        val result = rest.postForEntity("/v1/workflows", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.HTTP_MISSING_PARAMETER, result.body?.error?.code)
        assertEquals(HttpHeader.TENANT_ID, result.body?.error?.parameter?.name)
        assertEquals(ParameterType.PARAMETER_TYPE_HEADER, result.body?.error?.parameter?.type)
    }

    @Test
    fun `invalid workflow`() {
        val result = rest.postForEntity(
            "/v1/workflows",
            ImportWorkflowRequest(WorkflowData(name = "foo")),
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_NOT_VALID, result.body?.error?.code)
    }

    @Test
    fun `invalid form`() {
        val xrequest = ImportWorkflowRequest(
            workflow = WorkflowData(
                name = "new",
                description = "This is a new workflow",
                parameters = listOf("PARAM_1 ", "PARAM_2"),
                activities = listOf(
                    ActivityData(name = "START", type = ActivityType.START),
                    ActivityData(
                        name = "INVOICE",
                        title = "Invoicing...",
                        description = "SAGE create an invoice",
                        type = ActivityType.USER,
                        input = mapOf("foo" to "bar", "a" to "b"),
                        output = mapOf("x" to "y"),
                        form = "xxxx",
                        role = "accountant",
                    ),
                    ActivityData(
                        name = "STOP",
                        type = ActivityType.END,
                    ),
                ),
                flows = listOf(
                    FlowData(from = "START", to = "INVOICE"),
                    FlowData(from = "INVOICE", to = "STOP"),
                )
            )
        )

        val result = rest.postForEntity(
            "/v1/workflows",
            xrequest,
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `form of another tenant`() {
        val xrequest = ImportWorkflowRequest(
            workflow = WorkflowData(
                name = "new",
                description = "This is a new workflow",
                parameters = listOf("PARAM_1 ", "PARAM_2"),
                activities = listOf(
                    ActivityData(name = "START", type = ActivityType.START),
                    ActivityData(
                        name = "INVOICE",
                        title = "Invoicing...",
                        description = "SAGE create an invoice",
                        type = ActivityType.USER,
                        input = mapOf("foo" to "bar", "a" to "b"),
                        form = "f-200",
                        role = "accountant",
                    ),
                    ActivityData(
                        name = "STOP",
                        type = ActivityType.END,
                    ),
                ),
                flows = listOf(
                    FlowData(from = "START", to = "INVOICE"),
                    FlowData(from = "INVOICE", to = "STOP"),
                )
            )
        )

        val result = rest.postForEntity(
            "/v1/workflows",
            xrequest,
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `duplicate workflow name`() {
        val result = rest.postForEntity(
            "/v1/workflows",
            request.copy(
                workflow = request.workflow.copy(name = "W-110")
            ),
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_DUPLICATE_NAME, result.body?.error?.code)
    }

    @Test
    fun `workflow with instances`() {
        val result = rest.postForEntity(
            "/v1/workflows/120",
            request,
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_HAS_INSTANCES, result.body?.error?.code)
    }
}
