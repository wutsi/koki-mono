package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.Participant
import com.wutsi.koki.workflow.server.dao.ParameterRepository
import com.wutsi.koki.workflow.server.dao.ParticipantRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
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
class CreateWorkflowInstanceEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var instanceDao: WorkflowInstanceRepository

    @Autowired
    private lateinit var participanDao: ParticipantRepository

    @Autowired
    private lateinit var parameterDao: ParameterRepository

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
        assertEquals(request.workflowId, instance.workflow.id)
        assertEquals(request.approverUserId, instance.approver?.id)
        assertEquals(fmt.format(request.startAt), fmt.format(instance.startAt))
        assertEquals(fmt.format(request.dueAt), fmt.format(instance.dueAt))
        assertNull(instance.startedAt)
        assertNull(instance.doneAt)

        val parameters = parameterDao.findByInstance(instance).sortedBy { it.name }
        assertEquals(2, parameters.size)

        assertEquals("PARAM_1", parameters[0].name)
        assertEquals("val1", parameters[0].value)

        assertEquals("PARAM_2", parameters[1].name)
        assertEquals("val2", parameters[1].value)

        val participants = participanDao.findByInstance(instance).sortedBy { it.role.id }

        assertEquals(2, participants.size)

        assertEquals(10L, participants[0].role.id)
        assertEquals(100L, participants[0].user.id)

        assertEquals(11L, participants[1].role.id)
        assertEquals(101L, participants[1].user.id)
    }

    @Test
    fun `create with no approval an no roles`() {
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
        assertEquals(req.workflowId, instance.workflow.id)
        assertNull(instance.approver)

        val parameters = parameterDao.findByInstance(instance).sortedBy { it.name }
        assertEquals(0, parameters.size)

        val participants = participanDao.findByInstance(instance).sortedBy { it.role.id }
        assertEquals(0, participants.size)
    }

    @Test
    fun `missing approver`() {
        val xrequest = request.copy(approverUserId = null)
        val result = rest.postForEntity("/v1/workflow-instances", xrequest, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_APPROVER_MISSING, result.body?.error?.code)
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
    fun `missing participant`() {
        val xrequest = request.copy(participants = listOf(Participant(roleId = 10L, userId = 100L)))
        val result = rest.postForEntity("/v1/workflow-instances", xrequest, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_PARTICIPANT_MISSING, result.body?.error?.code)
    }

    @Test
    fun `invalid participant`() {
        val xrequest = request.copy(
            participants = listOf(
                Participant(roleId = 10L, userId = 100L),
                Participant(roleId = 11L, userId = 101L),
                Participant(roleId = 12L, userId = 102L),
            ),
        )
        val result = rest.postForEntity("/v1/workflow-instances", xrequest, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_PARTICIPANT_NOT_VALID, result.body?.error?.code)
    }

    @Test
    fun `workflow not active`() {
        val xrequest = request.copy(workflowId = 300L)
        val result = rest.postForEntity("/v1/workflow-instances", xrequest, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_NOT_ACTIVE, result.body?.error?.code)
    }

    @Test
    fun `startAt in past`() {
        val xrequest = request.copy(startAt = DateUtils.addDays(Date(), -7))
        val result = rest.postForEntity("/v1/workflow-instances", xrequest, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
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
}
