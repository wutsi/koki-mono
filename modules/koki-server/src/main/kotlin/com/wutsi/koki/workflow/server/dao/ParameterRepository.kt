package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.server.domain.ParameterEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ParameterRepository : CrudRepository<ParameterEntity, Long> {
    fun findByInstance(instance: WorkflowInstanceEntity): List<ParameterEntity>
}
