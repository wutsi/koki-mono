package com.wutsi.koki.form.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.form.dto.FormDataStatus
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.SubmitFormDataResponse
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.server.dao.FormDataRepository
import com.wutsi.koki.form.server.dao.FormSubmissionRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import kotlin.test.assertEquals

@Component
@Sql(value = ["/db/test/clean.sql", "/db/test/form/SubmitFormDataEndpoint.sql"])
class SubmitFormDataEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: FormDataRepository

    @Autowired
    private lateinit var submissionDao: FormSubmissionRepository

    @MockitoBean
    private lateinit var eventPublisher: EventPublisher

    private val request = SubmitFormDataRequest(
        formId = "100",
        workflowInstanceId = UUID.randomUUID().toString(),
        activityInstanceId = UUID.randomUUID().toString(),
        data = mapOf(
            "A" to "aa",
            "B" to "bb"
        )
    )

    @Test
    fun submit() {
        val result = rest.postForEntity("/v1/form-data", request, SubmitFormDataResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val formDataId = result.body!!.formDataId
        val formData = dao.findById(formDataId).get()
        assertEquals(TENANT_ID, formData.tenantId)
        assertEquals(request.formId, formData.formId)
        assertEquals(request.workflowInstanceId, formData.workflowInstanceId)
        assertEquals(FormDataStatus.SUBMITTED, formData.status)
        assertEquals("{\"A\": \"aa\", \"B\": \"bb\"}", formData.data)

        val submissions = submissionDao.findByFormId(request.formId)
        assertEquals(1, submissions.size)
        assertEquals(TENANT_ID, submissions[0].tenantId)
        assertEquals(request.formId, submissions[0].formId)
        assertEquals(request.workflowInstanceId, submissions[0].workflowInstanceId)
        assertEquals(request.activityInstanceId, submissions[0].activityInstanceId)
        assertEquals("{\"A\": \"aa\", \"B\": \"bb\"}", submissions[0].data)

        val event = argumentCaptor<FormSubmittedEvent>()
        verify(eventPublisher).publish(event.capture())
        assertEquals(request.formId, event.firstValue.formId)
        assertEquals(formData.id, event.firstValue.formDataId)
        assertEquals(request.activityInstanceId, event.firstValue.activityInstanceId)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
    }

    @Test
    fun update() {
        val xrequest = request.copy(workflowInstanceId = "wi-100")

        val result = rest.postForEntity("/v1/form-data", xrequest, SubmitFormDataResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val formDataId = result.body!!.formDataId
        assertEquals("10011", formDataId)

        val formData = dao.findById(formDataId).get()
        assertEquals(xrequest.workflowInstanceId, formData.workflowInstanceId)
        assertEquals("{\"A\": \"aa\", \"B\": \"bb\", \"X\": \"xx\"}", formData.data)

        val submissions = submissionDao.findByFormId(formData.formId)
        assertEquals(1, submissions.size)
        assertEquals(TENANT_ID, submissions[0].tenantId)
        assertEquals(xrequest.formId, submissions[0].formId)
        assertEquals(xrequest.workflowInstanceId, submissions[0].workflowInstanceId)
        assertEquals(xrequest.activityInstanceId, submissions[0].activityInstanceId)
        assertEquals("{\"A\": \"aa\", \"B\": \"bb\"}", submissions[0].data)

        val event = argumentCaptor<FormSubmittedEvent>()
        verify(eventPublisher).publish(event.capture())
        assertEquals(request.formId, event.firstValue.formId)
        assertEquals(formData.id, event.firstValue.formDataId)
        assertEquals(xrequest.activityInstanceId, event.firstValue.activityInstanceId)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
    }
}
