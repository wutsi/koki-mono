package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.server.domain.ParticipantEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ParticipantRepository : CrudRepository<ParticipantEntity, Long> {
    fun findByInstance(instance: WorkflowInstanceEntity): List<ParticipantEntity>
}
