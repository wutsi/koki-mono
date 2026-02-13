package com.wutsi.koki.listing.server.endpoint

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.server.service.CqsBatchService
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/ComputeAllCqsEndpoint.sql"])
class ComputeAllCqsEndpointTest : AuthorizationAwareEndpointTest() {
    @MockitoBean
    private lateinit var batchService: CqsBatchService

    @Test
    fun `compute all CQS returns 202 Accepted`() {
        // WHEN
        val response = rest.postForEntity("/v1/listings/cqs", null, Void::class.java)

        // THEN
        assertEquals(HttpStatus.ACCEPTED, response.statusCode)

        Thread.sleep(100)
        verify(batchService).computeAll(TENANT_ID)
    }
}
