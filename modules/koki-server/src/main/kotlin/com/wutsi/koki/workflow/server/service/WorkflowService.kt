package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.workflow.server.dao.WorkflowRepository
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class WorkflowService(private val dao: WorkflowRepository) {
    @Transactional
    fun save(workflow: WorkflowEntity) {
        workflow.modifiedAt = Date()
        dao.save(workflow)
    }
}
