package com.wutsi.koki.tenant.server.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.form.dto.FormDataStatus
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.SubmitFormDataResponse
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.server.dao.FormDataRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import kotlin.test.assertEquals

@Component
@Sql(value = ["/db/test/clean.sql", "/db/test/form/SubmitFormDataEndpoint.sql"])
class SubmitFormDataEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: FormDataRepository

    @MockBean
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
        assertEquals(USER_ID, formData.userId)
        assertEquals(request.formId, formData.form.id)
        assertEquals(request.activityInstanceId, formData.activityInstanceId)
        assertEquals(request.workflowInstanceId, formData.workflowInstanceId)
        assertEquals(FormDataStatus.SUBMITTED, formData.status)
        assertEquals("{\"A\": \"aa\", \"B\": \"bb\"}", formData.data)

        val event = argumentCaptor<FormSubmittedEvent>()
        verify(eventPublisher).publish(event.capture())
        assertEquals(request.formId, event.firstValue.formId)
        assertEquals(formData.id, event.firstValue.formDataId)
    }

    @Test
    fun `no user`() {
        anonymousUser = true

        val result = rest.postForEntity("/v1/form-data", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)

        verify(eventPublisher, never()).publish(any())
    }
}
