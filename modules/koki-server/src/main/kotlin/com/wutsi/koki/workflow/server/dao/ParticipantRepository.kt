package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.server.domain.ParticipantEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ParticipantRepository : CrudRepository<ParticipantEntity, Long> {
    fun findByWorkflowInstanceId(workflowInstanceId: String): List<ParticipantEntity>

    fun findByWorkflowInstanceIdAndRoleId(workflowInstanceId: String, roleId: Long): ParticipantEntity?

    fun findByUserIdIn(userId: List<Long>): List<ParticipantEntity>
}
