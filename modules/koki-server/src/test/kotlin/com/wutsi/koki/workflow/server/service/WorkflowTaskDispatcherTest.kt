package com.wutsi.koki.workflow.server.service

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.ParticipantRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.ParticipantEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertNull

class WorkflowTaskDispatcherTest {
    private val userService = mock<UserService>()
    private val activityInstanceDao = mock<ActivityInstanceRepository>()
    private val participantDao = mock<ParticipantRepository>()
    private val workflowInstanceDao = mock<WorkflowInstanceRepository>()
    private val dispatcher =
        WorkflowTaskDispatcher(userService, participantDao, activityInstanceDao, workflowInstanceDao)

    @Test
    fun `no user`() {
        doReturn(emptyList<UserEntity>()).whenever(userService)
            .search(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        val result = dispatcher.dispatch(11, 1)

        assertNull(result)
    }

    @Test
    fun `one user`() {
        val user = UserEntity(id = 11L)
        doReturn(listOf(user)).whenever(userService)
            .search(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        val result = dispatcher.dispatch(1, 11)

        assertEquals(user.id, result?.id)
    }

    @Test
    fun `multiple users`() {
        val users = listOf(
            UserEntity(id = 11L),
            UserEntity(id = 12L),
            UserEntity(id = 13L),
            UserEntity(id = 14L),
        )
        doReturn(users).whenever(userService)
            .search(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        val instanceByAssignee = listOf(
            WorkflowInstanceEntity(id = "aa", participants = listOf(ParticipantEntity(userId = 11L))),
            WorkflowInstanceEntity(id = "bb", participants = listOf(ParticipantEntity(userId = 11L))),
            WorkflowInstanceEntity(id = "cc", participants = listOf(ParticipantEntity(userId = 12L))),
            WorkflowInstanceEntity(id = "dd", participants = listOf(ParticipantEntity(userId = 12L))),
            WorkflowInstanceEntity(id = "ee", participants = listOf(ParticipantEntity(userId = 12L))),
            WorkflowInstanceEntity(id = "ff", participants = listOf(ParticipantEntity(userId = 11L))),
            WorkflowInstanceEntity(id = "gg", participants = listOf(ParticipantEntity(userId = 11L))),
            WorkflowInstanceEntity(id = "gg", participants = listOf(ParticipantEntity(userId = 14L))),
            WorkflowInstanceEntity(id = "hh", participants = listOf(ParticipantEntity(userId = 13L))),
            WorkflowInstanceEntity(id = "ii", participants = listOf(ParticipantEntity(userId = 13L))),
        )
        doReturn(instanceByAssignee).whenever(workflowInstanceDao)
            .findByIdInAndStatusInAndTenantId(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        val instanceByApprover = listOf(
            ActivityInstanceEntity(approverId = 11L),
            ActivityInstanceEntity(approverId = 12L),
            ActivityInstanceEntity(approverId = 13L),
        )
        doReturn(instanceByApprover).whenever(activityInstanceDao)
            .findByApproverIdInAndStatusInAndTenantId(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
        val result = dispatcher.dispatch(1, 11)

        assertEquals(14L, result?.id)
    }
}
