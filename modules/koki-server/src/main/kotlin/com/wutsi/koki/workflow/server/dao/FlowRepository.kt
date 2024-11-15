package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.FlowEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FlowRepository : CrudRepository<FlowEntity, String> {
    fun findByWorkflowId(workflowId: Long): List<FlowEntity>

    fun findByFromAndTo(from: ActivityEntity, to: ActivityEntity): FlowEntity?
}
