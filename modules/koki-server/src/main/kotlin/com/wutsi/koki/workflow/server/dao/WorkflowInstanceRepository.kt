package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkflowInstanceRepository : CrudRepository<WorkflowInstanceEntity, String> {
    fun countByWorkflowId(workflowId: Long): Long?

    fun findByWorkflowId(workflowId: Long): List<WorkflowInstanceEntity>

    fun findByIdInAndStatusInAndTenantId(
        id: List<String>,
        status: List<WorkflowStatus>,
        tenantId: Long
    ): List<WorkflowInstanceEntity>
}
