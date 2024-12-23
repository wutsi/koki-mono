package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ActivityInstanceRepository : CrudRepository<ActivityInstanceEntity, String> {
    fun findByWorkflowInstanceId(workflowInstanceId: String): List<ActivityInstanceEntity>

    fun findByIdInAndTenantId(ids: List<String>, tenantId: Long): List<ActivityInstanceEntity>

    fun findByApproverIdInAndStatusInAndTenantId(
        approverId: List<Long>,
        status: List<WorkflowStatus>,
        tenantId: Long
    ): List<ActivityInstanceEntity>
}
