package com.wutsi.koki.workflow.server.service

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertNull

class WorkflowTaskDispatcherTest {
    private val userService = mock<UserService>()
    private val activityInstanceService = mock<ActivityInstanceService>()
    private val dispatcher = WorkflowTaskDispatcher(userService, activityInstanceService)

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
            )

        val instanceByAssignee = listOf(
            ActivityInstanceEntity(assigneeId = 11L),
            ActivityInstanceEntity(assigneeId = 11L),
            ActivityInstanceEntity(assigneeId = 12L),
            ActivityInstanceEntity(assigneeId = 12L),
            ActivityInstanceEntity(assigneeId = 12L),
            ActivityInstanceEntity(assigneeId = 11L),
            ActivityInstanceEntity(assigneeId = 11L),
            ActivityInstanceEntity(assigneeId = 14L),
            ActivityInstanceEntity(assigneeId = 13L),
            ActivityInstanceEntity(assigneeId = 13L),
        )
        doReturn(instanceByAssignee).whenever(activityInstanceService)
            .search(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        val instanceByApprover = listOf(
            ActivityInstanceEntity(approverId = 11L),
            ActivityInstanceEntity(approverId = 12L),
            ActivityInstanceEntity(approverId = 13L),
        )
        doReturn(instanceByApprover).whenever(activityInstanceService)
            .search(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
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

        assertEquals(14L, result?.id)
    }
}
