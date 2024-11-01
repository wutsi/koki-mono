package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkflowRepository : CrudRepository<WorkflowEntity, Long>
