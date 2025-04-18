package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.account.dto.GetInvitationResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/account/GetInvitationEndpoint.sql"])
class GetInvitationEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/invitations/101", GetInvitationResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val invitation = response.body!!.invitation
        assertEquals(100L, invitation.accountId)
        assertEquals(11L, invitation.createdById)
    }

    @Test
    fun badId() {
        val response = rest.getForEntity("/v1/invitations/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.INVITATION_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun badTenant() {
        val response = rest.getForEntity("/v1/invitations/201", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.INVITATION_NOT_FOUND, response.body?.error?.code)
    }
}
