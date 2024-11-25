package com.wutsi.koki.tenant.server.server.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.Participant
import com.wutsi.koki.workflow.server.dao.ParticipantRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import com.wutsi.koki.workflow.server.dao.WorkflowRepository
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/CreateWorkflowInstanceEndpoint.sql"])
class CreateWorkflowInstanceEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var instanceDao: WorkflowInstanceRepository

    @Autowired
    private lateinit var participanDao: ParticipantRepository

    @Autowired
    protected lateinit var workflowDao: WorkflowRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    val request = CreateWorkflowInstanceRequest(
        workflowId = 100L,
        startAt = DateUtils.addDays(Date(), 7),
        dueAt = DateUtils.addDays(Date(), 14),
        approverUserId = 100L,
        parameters = mapOf("PARAM_1" to "val1", "PARAM_2" to "val2"),
        participants = listOf(
            Participant(roleId = 10L, userId = 100L),
            Participant(roleId = 11L, userId = 101L),
        ),
    )

    @Test
    fun create() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val result = rest.postForEntity("/v1/workflow-instances", request, CreateWorkflowInstanceResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val instanceId = result.body!!.workflowInstanceId
        val instance = instanceDao.findById(instanceId).get()
        assertEquals(request.workflowId, instance.workflowId)
        assertEquals(request.approverUserId, instance.approverId)
        assertEquals(fmt.format(request.startAt), fmt.format(instance.startAt))
        assertEquals(fmt.format(request.dueAt), fmt.format(instance.dueAt))
        assertNull(instance.startedAt)
        assertNull(instance.doneAt)
        assertEquals(USER_ID, instance.createdById)

        val parameters = objectMapper.readValue(instance.parameters, Map::class.java)
        assertEquals(2, parameters.size)
        assertEquals("val1", parameters["PARAM_1"])
        assertEquals("val2", parameters["PARAM_2"])

        val participants = participanDao.findByWorkflowInstanceId(instanceId).sortedBy { it.roleId }

        assertEquals(2, participants.size)

        assertEquals(10L, participants[0].roleId)
        assertEquals(100L, participants[0].userId)

        assertEquals(11L, participants[1].roleId)
        assertEquals(101L, participants[1].userId)
    }

    @Test
    fun `create with no approval an no roles and anonymously`() {
        anonymousUser = true
        val req = CreateWorkflowInstanceRequest(
            workflowId = 200L,
            startAt = DateUtils.addDays(Date(), 7),
            dueAt = DateUtils.addDays(Date(), 14),
            approverUserId = null,
            parameters = emptyMap(),
            participants = emptyList(),
        )
        val result = rest.postForEntity("/v1/workflow-instances", req, CreateWorkflowInstanceResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val instanceId = result.body!!.workflowInstanceId

        val instance = instanceDao.findById(instanceId).get()
        assertEquals(req.workflowId, instance.workflowId)
        assertNull(instance.approverId)
        assertNull(instance.createdById)

        val parameters = objectMapper.readValue(instance.parameters, Map::class.java)
        assertEquals(0, parameters.size)

        val participants = participanDao.findByWorkflowInstanceId(instanceId).sortedBy { it.roleId }
        assertEquals(0, participants.size)
    }

    @Test
    fun `missing parameter`() {
        val xrequest = request.copy(parameters = mapOf("PARAM_1" to "val1"))
        val result = rest.postForEntity("/v1/workflow-instances", xrequest, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_PARAMETER_MISSING, result.body?.error?.code)
    }

    @Test
    fun `invalid parameter`() {
        val xrequest = request.copy(parameters = mapOf("PARAM_1" to "val1", "PARAM_2" to "val2", "X" to "Y"))
        val result = rest.postForEntity("/v1/workflow-instances", xrequest, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_PARAMETER_NOT_VALID, result.body?.error?.code)
    }

    @Test
    fun `no participant`() {
        val xrequest = request.copy(participants = emptyList())
        val result = rest.postForEntity("/v1/workflow-instances", xrequest, CreateWorkflowInstanceResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val instanceId = result.body!!.workflowInstanceId
        val instance = instanceDao.findById(instanceId).get()
        assertEquals(xrequest.workflowId, instance.workflowId)

        val participants = participanDao.findByWorkflowInstanceId(instanceId).sortedBy { it.roleId }
        assertEquals(0, participants.size)
    }

    @Test
    fun `workflow not active`() {
        val xrequest = request.copy(workflowId = 300L)
        val result = rest.postForEntity("/v1/workflow-instances", xrequest, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_NOT_ACTIVE, result.body?.error?.code)
    }

    @Test
    fun `no dueAt`() {
        val xrequest = request.copy(dueAt = null)
        val result = rest.postForEntity("/v1/workflow-instances", xrequest, CreateWorkflowInstanceResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val instanceId = result.body!!.workflowInstanceId
        val instance = instanceDao.findById(instanceId).get()
        assertNull(instance.dueAt)
    }

    @Test
    fun `update workflow instance count`() {
        val result = rest.postForEntity(
            "/v1/workflow-instances",
            request.copy(workflowId = 400),
            CreateWorkflowInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflow = workflowDao.findById(400).get()
        assertEquals(6, workflow.workflowInstanceCount)
    }
}
