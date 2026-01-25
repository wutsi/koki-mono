package com.wutsi.koki.agent.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.agent.dto.event.AgentCreatedEvent
import com.wutsi.koki.agent.server.domain.AgentEntity
import com.wutsi.koki.agent.server.service.AgentService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.dto.InvitationType
import com.wutsi.koki.tenant.dto.event.UserCreatedEvent
import com.wutsi.koki.tenant.server.domain.InvitationEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.InvitationService
import com.wutsi.koki.tenant.server.service.UserService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class AgentUserCreatedEventHandlerTest {
    private val userService = mock<UserService>()
    private val invitationService = mock<InvitationService>()
    private val agentService = mock<AgentService>()
    private val logger = DefaultKVLogger()
    private val publisher = mock<Publisher>()
    private val handler = AgentUserCreatedEventHandler(
        userService = userService,
        invitationService = invitationService,
        agentService = agentService,
        logger = logger,
        publisher = publisher,
    )

    val tenantId: Long = 1L
    val userId: Long = 11L
    val invitationId: String = "111"

    @BeforeEach
    fun setUp() {
        doReturn(AgentEntity(id = 77L)).whenever(agentService).create(any(), any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun `handle user created from invitation to an AGENT`() {
        // GIVEN
        setupUser(userId, invitationId)
        setupInvigation(invitationId, InvitationType.AGENT)

        val agent = AgentEntity(id = 77L, tenantId = tenantId)
        doReturn(agent).whenever(agentService).create(any(), any())

        // WHEN
        handler.handle(
            UserCreatedEvent(
                userId = userId,
                invitationId = invitationId,
                tenantId = tenantId
            )
        )

        // THEN
        verify(agentService).create(userId, tenantId)

        val event = argumentCaptor<AgentCreatedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(agent.id, event.firstValue.agentId)
        assertEquals(agent.tenantId, event.firstValue.tenantId)
    }

    @Test
    fun `handle user created from invitation to an UNKNOWN`() {
        setupUser(userId, invitationId)
        setupInvigation(invitationId, InvitationType.UNKNOWN)

        handler.handle(
            UserCreatedEvent(
                userId = userId,
                invitationId = invitationId,
                tenantId = tenantId
            )
        )

        verify(agentService, never()).create(any(), any())
        verify(publisher, never()).publish(any())
    }

    @Test
    fun `handle user created without invitation`() {
        setupUser(userId, null)

        handler.handle(
            UserCreatedEvent(
                userId = userId,
                invitationId = null,
                tenantId = tenantId
            )
        )

        verify(agentService, never()).create(any(), any())
        verify(publisher, never()).publish(any())
    }

    private fun setupUser(id: Long, invitationId: String? = null): UserEntity {
        val user = UserEntity(id = id, invitationId = invitationId, tenantId = tenantId)
        doReturn(user).whenever(userService).get(any(), any())
        return user
    }

    private fun setupInvigation(id: String, type: InvitationType): InvitationEntity {
        val invitation = InvitationEntity(id = id, type = type, tenantId = tenantId)
        doReturn(invitation).whenever(invitationService).get(any(), any())
        return invitation
    }
}
