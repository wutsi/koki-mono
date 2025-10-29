package com.wutsi.koki.tenant.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.dto.CreateInvitationRequest
import com.wutsi.koki.tenant.dto.CreateInvitationResponse
import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.InvitationType
import com.wutsi.koki.tenant.dto.event.InvitationCreatedEvent
import com.wutsi.koki.tenant.server.dao.InvitationRepository
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql"])
class CreateInvitationEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: InvitationRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @Value("\${koki.module.invitation.ttl-days}")
    private lateinit var ttlDays: Integer

    @Test
    fun create() {
        val df = SimpleDateFormat("yyyy-MM-dd")

        val request = CreateInvitationRequest(
            displayName = "Ray Sponsible",
            email = "ray.sponsible@gmail.com",
            type = InvitationType.AGENT,
        )

        val result = rest.postForEntity("/v1/invitations", request, CreateInvitationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val invitationId = result.body!!.invitationId
        val invitation = dao.findById(invitationId).get()
        assertEquals(request.displayName, invitation.displayName)
        assertEquals(request.email, invitation.email)
        assertEquals(request.type, invitation.type)
        assertEquals(TENANT_ID, invitation.tenantId)
        assertEquals(USER_ID, invitation.createdById)
        assertEquals(InvitationStatus.PENDING, invitation.status)
        assertEquals(
            df.format(DateUtils.addDays(invitation.createdAt, ttlDays.toInt())),
            df.format(invitation.expiresAt)
        )

        val event = argumentCaptor<InvitationCreatedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(invitation.id, event.firstValue.invitationId)
        assertEquals(invitation.tenantId, event.firstValue.tenantId)
    }
}
