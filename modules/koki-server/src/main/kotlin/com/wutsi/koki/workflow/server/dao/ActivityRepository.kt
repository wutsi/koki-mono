package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.server.domain.ActivityEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ActivityRepository : CrudRepository<ActivityEntity, Long> {
    fun findByWorkflowId(workflowId: Long): List<ActivityEntity>

    fun findByNameAndWorkflowId(code: String, workflowId: Long): ActivityEntity?
}
