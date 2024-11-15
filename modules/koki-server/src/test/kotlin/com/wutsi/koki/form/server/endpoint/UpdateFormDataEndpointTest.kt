package com.wutsi.koki.tenant.server.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.form.event.FormUpdatedEvent
import com.wutsi.koki.form.server.dao.FormDataRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/UpdateFormDataEndpoint.sql"])
class UpdateFormDataEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: FormDataRepository

    private val request = UpdateFormDataRequest(
        data = mapOf(
            "A" to "aa1",
            "B" to "bb1"
        ),
        activityInstanceId = UUID.randomUUID().toString()
    )

    @MockBean
    private lateinit var eventPublisher: EventPublisher

    @Test
    fun update() {
        val result = rest.postForEntity("/v1/form-data/10011", request, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val formData = dao.findById("10011").get()
        assertEquals("{\"A\": \"aa1\", \"B\": \"bb1\"}", formData.data)

        val event = argumentCaptor<FormUpdatedEvent>()
        verify(eventPublisher).publish(event.capture())
        assertEquals(formData.form.id, event.firstValue.formId)
        assertEquals(formData.id, event.firstValue.formDataId)
        assertEquals(request.activityInstanceId, event.firstValue.activityInstanceId)
    }

    @Test
    fun `not found`() {
        val result = rest.postForEntity("/v1/form-data/xx", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_DATA_NOT_FOUND, result.body?.error?.code)

        verify(eventPublisher, never()).publish(any())
    }

    @Test
    fun `another tenant`() {
        val result = rest.postForEntity("/v1/form-data/20022", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_DATA_NOT_FOUND, result.body?.error?.code)
    }
}
