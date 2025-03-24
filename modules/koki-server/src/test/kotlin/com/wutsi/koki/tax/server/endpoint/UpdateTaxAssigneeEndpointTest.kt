package com.wutsi.koki.tax.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tax.dto.UpdateTaxAssigneeRequest
import com.wutsi.koki.tax.dto.event.TaxAssigneeChangedEvent
import com.wutsi.koki.tax.server.dao.TaxRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/UpdateTaxAssigneeEndpoint.sql"])
class UpdateTaxAssigneeEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TaxRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    private val request = UpdateTaxAssigneeRequest(assigneeId = 222L)

    @Test
    fun update() {
        val result = rest.postForEntity("/v1/taxes/100/assignee", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxId = 100L
        val tax = dao.findById(taxId).get()
        assertEquals(request.assigneeId, tax.assigneeId)
        assertEquals(USER_ID, tax.modifiedById)

        val event = argumentCaptor<TaxAssigneeChangedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(request.assigneeId, event.firstValue.assigneeId)
        assertEquals(taxId, event.firstValue.taxId)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
    }

    @Test
    fun `same status`() {
        val now = System.currentTimeMillis()
        Thread.sleep(5000)

        val result = rest.postForEntity(
            "/v1/taxes/110/assignee",
            request.copy(assigneeId = 111L),
            Any::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val tax = dao.findById(110).get()
        assertEquals(true, tax.modifiedAt.time < now)

        verify(publisher, never()).publish(any())
    }
}
