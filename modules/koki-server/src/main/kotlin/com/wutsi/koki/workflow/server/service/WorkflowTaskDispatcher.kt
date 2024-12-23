package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.springframework.stereotype.Service

@Service
class WorkflowTaskDispatcher(
    private val userService: UserService,
    private val activityInstanceService: ActivityInstanceService,
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
        val taskByUser = activityInstanceService.search(
            tenantId = tenantId,
            assigneeIds = userIds,
            status = WorkflowStatus.RUNNING,
            limit = Integer.MAX_VALUE,
        ).groupBy { instance -> instance.assigneeId }

        val approvalByUser = activityInstanceService.search(
            tenantId = tenantId,
            approverIds = userIds,
            status = WorkflowStatus.RUNNING,
            limit = Integer.MAX_VALUE,
        ).groupBy { instance -> instance.approverId }

        // Select the user having the lowest work load
        return users.sortedBy { user ->
            (taskByUser[user.id]?.size ?: 0) + (approvalByUser[user.id]?.size ?: 0)
        }.firstOrNull()
    }
}
