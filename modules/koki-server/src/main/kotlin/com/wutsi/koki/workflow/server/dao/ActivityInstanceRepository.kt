package com.wutsi.koki.workflow.server.dao

import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ActivityInstanceRepository : CrudRepository<ActivityInstanceEntity, String> {
    fun findByInstance(intance: WorkflowInstanceEntity): List<ActivityInstanceEntity>

    fun findByActivityAndInstance(activity: ActivityEntity, instance: WorkflowInstanceEntity): ActivityInstanceEntity?
}
