package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ActivityRepository : CrudRepository<ActivityEntity, Long> {
    fun findByWorkflow(workflow: WorkflowEntity): List<ActivityEntity>

    fun findByNameAndWorkflow(code: String, workflow: WorkflowEntity): ActivityEntity?

    fun findByNameInAndWorkflow(code: List<String>, workflow: WorkflowEntity): List<ActivityEntity>
}
