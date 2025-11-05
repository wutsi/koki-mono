package com.wutsi.koki.agent.server.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.agent.server.domain.AgentEntity
import com.wutsi.koki.agent.server.service.AgentService
import com.wutsi.koki.platform.logger.DefaultKVLogger
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

class UserCreatedEventHandlerTest {
    private val userService = mock<UserService>()
    private val invitationService = mock<InvitationService>()
    private val agentService = mock<AgentService>()
    private val logger = DefaultKVLogger()
    private val handler = UserCreatedEventHandler(
        userService = userService,
        invitationService = invitationService,
        agentService = agentService,
        logger = logger,
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
        setupUser(userId, invitationId)
        setupInvigation(invitationId, InvitationType.AGENT)

        handler.handle(
            UserCreatedEvent(
                userId = userId,
                invitationId = invitationId,
                tenantId = tenantId
            )
        )

        verify(agentService).create(userId, tenantId)
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
