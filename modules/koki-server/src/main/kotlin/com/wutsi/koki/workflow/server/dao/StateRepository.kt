package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.server.domain.StateEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StateRepository : CrudRepository<StateEntity, Long> {
    fun findByInstance(instance: WorkflowInstanceEntity): List<StateEntity>

    fun findByNameAndInstance(name: String, instance: WorkflowInstanceEntity): StateEntity?
}
