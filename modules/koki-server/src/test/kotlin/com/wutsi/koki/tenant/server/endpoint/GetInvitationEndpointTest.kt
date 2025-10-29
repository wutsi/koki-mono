package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.GetInvitationResponse
import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.InvitationType
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/GetInvitationEndpoint.sql"])
class GetInvitationEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/invitations/100", GetInvitationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val invitation = result.body!!.invitation
        assertEquals("Ray Sponsible", invitation.displayName)
        assertEquals("ray.sponsible@gmail.com", invitation.email)
        assertEquals(InvitationStatus.PENDING, invitation.status)
        assertEquals(InvitationType.AGENT, invitation.type)
    }

    @Test
    fun deleted() {
        val result = rest.getForEntity("/v1/invitations/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.INVITATION_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `not found`() {
        val result = rest.getForEntity("/v1/invitations/not-found", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.INVITATION_NOT_FOUND, result.body?.error?.code)
    }
}
