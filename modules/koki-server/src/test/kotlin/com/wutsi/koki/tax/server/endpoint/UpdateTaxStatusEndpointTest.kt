package com.wutsi.koki.tax.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tax.dto.TaxStatus
import com.wutsi.koki.tax.dto.UpdateTaxStatusRequest
import com.wutsi.koki.tax.dto.event.TaxStatusChangedEvent
import com.wutsi.koki.tax.server.dao.TaxRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/UpdateTaxStatusEndpoint.sql"])
class UpdateTaxStatusEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TaxRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    private val request = UpdateTaxStatusRequest(status = TaxStatus.SUBMITTING)

    @Test
    fun update() {
        val result = rest.postForEntity("/v1/taxes/100/status", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxId = 100L
        val tax = dao.findById(taxId).get()
        assertEquals(request.status, tax.status)
        assertEquals(USER_ID, tax.modifiedById)

        val event = argumentCaptor<TaxStatusChangedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(request.status, event.firstValue.status)
        assertEquals(taxId, event.firstValue.taxId)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
    }

    @Test
    fun `same status`() {
        val now = System.currentTimeMillis()
        Thread.sleep(5000)

        val result = rest.postForEntity(
            "/v1/taxes/110/status", request.copy(status = TaxStatus.GATHERING_DOCUMENTS), Any::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val tax = dao.findById(110).get()
        assertEquals(true, tax.modifiedAt.time < now)

        verify(publisher, never()).publish(any())
    }
}
