package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tenant.dto.SearchInvitationResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/SearchInvitationEndpoint.sql"])
class SearchInvitationEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/invitations", SearchInvitationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val invitations = result.body!!.invitations
        assertEquals(4, invitations.size)
    }

    @Test
    fun `by ids`() {
        val result = rest.getForEntity("/v1/invitations?id=100&id=101&id=200", SearchInvitationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val invitations = result.body!!.invitations
        assertEquals(2, invitations.size)
        assertEquals(true, invitations.map { inv -> inv.id }.containsAll(listOf("100", "101")))
    }

    @Test
    fun `by status`() {
        val result = rest.getForEntity(
            "/v1/invitations?status=PENDING&status=ACCEPTED",
            SearchInvitationResponse::class.java
        )

        val invitations = result.body!!.invitations
        assertEquals(3, invitations.size)
        assertEquals(true, invitations.map { inv -> inv.id }.containsAll(listOf("100", "101", "102")))
    }
}
