package com.wutsi.koki.account.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.account.dto.CreateInvitationRequest
import com.wutsi.koki.account.dto.CreateInvitationResponse
import com.wutsi.koki.account.dto.event.InvitationCreatedEvent
import com.wutsi.koki.account.server.dao.AccountRepository
import com.wutsi.koki.account.server.dao.InvitationRepository
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.platform.mq.Publisher
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/account/CreateInvitationEndpoint.sql"])
class CreateInvitationEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: InvitationRepository

    @Autowired
    private lateinit var accountDao: AccountRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    val request = CreateInvitationRequest(
        accountId = 100,
    )

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/invitations", request, CreateInvitationResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val invitation = dao.findById(response.body!!.invitationId).get()
        assertEquals(request.accountId, invitation.accountId)
        assertEquals(TENANT_ID, invitation.tenantId)
        assertEquals(USER_ID, invitation.createById)

        val account = accountDao.findById(request.accountId).get()
        assertEquals(invitation.id, account.invitationId)

        val event = argumentCaptor<InvitationCreatedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(invitation.id, event.firstValue.invitationId)
        assertEquals(invitation.tenantId, event.firstValue.tenantId)
    }

    @Test
    fun `create - invalid account`() {
        val response = rest.postForEntity("/v1/invitations", request.copy(accountId = 9999), ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, response.body?.error?.code)

        verify(publisher, never()).publish(any())
    }
}
