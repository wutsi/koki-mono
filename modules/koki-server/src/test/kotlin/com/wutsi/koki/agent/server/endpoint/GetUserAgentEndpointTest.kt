package com.wutsi.koki.agent.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.agent.dto.GetAgentResponse
import com.wutsi.koki.agent.dto.MetricPeriod
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.listing.dto.ListingType
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/agent/GetUserAgentEndpoint.sql"])
class GetUserAgentEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/users/11/agent", GetAgentResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val agent = response.body!!.agent
        assertEquals(11L, agent.userId)
        assertEquals(100, agent.totalSales)
        assertEquals(200, agent.totalRentals)
        assertEquals(50, agent.past12mSales)
        assertEquals(100, agent.past12mRentals)

        assertEquals(2, agent.metrics.size)
        assertEquals(ListingType.SALE, agent.metrics[0].listingType)
        assertEquals(MetricPeriod.OVERALL, agent.metrics[0].period)
        assertEquals(11, agent.metrics[0].total)
        assertEquals(1500L, agent.metrics[0].minPrice)
        assertEquals(15000L, agent.metrics[0].maxPrice)
        assertEquals(12500L, agent.metrics[0].averagePrice)
        assertEquals(115000L, agent.metrics[0].totalPrice)
        assertEquals("CAD", agent.metrics[0].currency)

        assertEquals(ListingType.SALE, agent.metrics[1].listingType)
        assertEquals(MetricPeriod.PAST_12M, agent.metrics[1].period)
        assertEquals(10, agent.metrics[1].total)
        assertEquals(500L, agent.metrics[1].minPrice)
        assertEquals(5000L, agent.metrics[1].maxPrice)
        assertEquals(2500L, agent.metrics[1].averagePrice)
        assertEquals(15000L, agent.metrics[1].totalPrice)
        assertEquals("CAD", agent.metrics[1].currency)
    }

    @Test
    fun notFound() {
        val response = rest.getForEntity("/v1/users/33/agent", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.AGENT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun otherTenant() {
        val response = rest.getForEntity("/v1/agents/22", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.AGENT_NOT_FOUND, response.body?.error?.code)
    }
}
