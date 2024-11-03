package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkflowInstanceRepository : CrudRepository<WorkflowInstanceEntity, Long>
