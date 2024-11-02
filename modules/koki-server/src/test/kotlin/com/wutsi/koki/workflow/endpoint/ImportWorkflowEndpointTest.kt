package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.error.dto.ParameterType
import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.ImportWorkflowRequest
import com.wutsi.koki.workflow.dto.ImportWorkflowResponse
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.dao.ActivityRepository
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

    private val request = ImportWorkflowRequest(
        workflow = WorkflowData(
            name = "new",
            description = "This is a new workflow",
            activities = listOf(
                ActivityData(name = "START", type = ActivityType.START),
                ActivityData(
                    name = "INVOICE",
                    description = "SAGE create an invoice",
                    type = ActivityType.SERVICE,
                    predecessors = listOf("START"),
                    tags = mapOf("foo" to "bar", "a" to "b"),
                    requiresApproval = true,
                    role = "accountant",
                ),
                ActivityData(
                    name = "STOP",
                    type = ActivityType.STOP,
                    predecessors = listOf("INVOICE")
                ),
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

        val activities = activityDao.findByWorkflow(workflow)
        assertEquals(3, activities.size)

        val aStart = activityDao.findByNameAndWorkflow("START", workflow)
        assertEquals("START", aStart?.name)
        assertEquals(request.workflow.activities[0].type, aStart?.type)
        assertEquals(true, aStart?.active)
        assertNull(aStart?.role)

        val aInvoice = activityDao.findByNameAndWorkflow("INVOICE", workflow)
        assertEquals("INVOICE", aInvoice?.name)
        assertEquals(request.workflow.activities[1].description, aInvoice?.description)
        assertEquals(request.workflow.activities[1].type, aInvoice?.type)
        assertEquals("foo=bar\na=b", aInvoice?.tags)
        assertEquals(true, aInvoice?.active)
        assertEquals(request.workflow.activities[1].requiresApproval, aInvoice?.requiresApproval)
        assertEquals(10L, aInvoice?.role?.id)

        val aEnd = activityDao.findByNameAndWorkflow("STOP", workflow)
        assertEquals("STOP", aEnd?.name)
        assertEquals(request.workflow.activities[2].type, aEnd?.type)
        assertEquals(true, aEnd?.active)
        assertNull(aEnd?.role)
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

        val activities = activityDao.findByWorkflow(workflow)
        assertEquals(4, activities.size)

        val aStart = activityDao.findByNameAndWorkflow("START", workflow)
        assertEquals("START", aStart?.name)
        assertEquals(request.workflow.activities[0].type, aStart?.type)
        assertEquals(true, aStart?.active)
        assertNull(aStart?.role)

        val aInvoice = activityDao.findByNameAndWorkflow("INVOICE", workflow)
        assertEquals("INVOICE", aInvoice?.name)
        assertEquals(request.workflow.activities[1].description, aInvoice?.description)
        assertEquals(request.workflow.activities[1].type, aInvoice?.type)
        assertEquals("foo=bar\na=b", aInvoice?.tags)
        assertEquals(true, aInvoice?.active)
        assertEquals(request.workflow.activities[1].requiresApproval, aInvoice?.requiresApproval)
        assertEquals(10L, aInvoice?.role?.id)

        val aEnd = activityDao.findByNameAndWorkflow("STOP", workflow)
        assertEquals("STOP", aEnd?.name)
        assertEquals(request.workflow.activities[2].type, aEnd?.type)
        assertEquals(true, aEnd?.active)
        assertNull(aEnd?.role)

        val aDeactivated = activityDao.findById(111L).get()
        assertEquals(false, aDeactivated.active)
        assertEquals(workflow.id, aDeactivated.workflow.id)
        assertEquals(11L, aDeactivated?.role?.id)
    }

    @Test
    fun `update workflow not found`() {
        val result = rest.postForEntity("/v1/workflows/999", ImportWorkflowRequest(), ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `update workflow of another tenant`() {
        val result = rest.postForEntity("/v1/workflows/200", ImportWorkflowRequest(), ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `update workflow of non numeric id`() {
        val result = rest.postForEntity("/v1/workflows/xxx", ImportWorkflowRequest(), ErrorResponse::class.java)

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
}
