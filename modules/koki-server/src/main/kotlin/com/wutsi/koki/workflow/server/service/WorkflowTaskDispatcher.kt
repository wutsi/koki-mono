package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.ParticipantRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import org.springframework.stereotype.Service

@Service
class WorkflowTaskDispatcher(
    private val userService: UserService,
    private val activityInstanceDao: ActivityInstanceRepository,
    private val participantDao: ParticipantRepository,
    private val workflowInstanceDao: WorkflowInstanceRepository,
) {
    fun dispatch(roleId: Long, tenantId: Long): UserEntity? {
        // Find all the users associated with the role
        val users = userService.search(
            tenantId = tenantId,
            roleIds = listOf(roleId),
            status = UserStatus.ACTIVE,
            limit = Integer.MAX_VALUE,
        )
        if (users.isEmpty()) {
            return null
        } else if (users.size == 1) {
            return users.first()
        }

        // Get the workload by user
        val userIds = users.mapNotNull { user -> user.id }
        val participants = participantDao.findByUserIdIn(userIds)
        val workflowInstanceIds = participants.map { participant -> participant.workflowInstanceId }.toSet()
        val tasks = workflowInstanceDao.findByIdInAndStatusInAndTenantId(
            tenantId = tenantId,
            status = listOf(WorkflowStatus.NEW, WorkflowStatus.RUNNING),
            id = workflowInstanceIds.toList()
        )
        val tasksByUser = tasks.flatMap { task ->
            task.participants.map { participant -> Pair(participant.userId, task) }
        }.groupBy { pair -> pair.first }

        val approvals = activityInstanceDao.findByApproverIdInAndStatusInAndTenantId(
            tenantId = tenantId,
            approverId = userIds,
            status = listOf(WorkflowStatus.NEW, WorkflowStatus.RUNNING),
        )
        val approvalsByUser = approvals.groupBy { instance -> instance.approverId }

        // Select the user having the lowest work load
        return users.sortedBy { user ->
            (tasksByUser[user.id]?.size ?: 0) + (approvalsByUser[user.id]?.size ?: 0)
        }.firstOrNull()
    }
}
