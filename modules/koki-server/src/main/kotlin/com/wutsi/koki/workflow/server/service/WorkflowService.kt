package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.server.dao.WorkflowRepository
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class WorkflowService(private val dao: WorkflowRepository) {
    fun get(id: Long, tenantId: Long): WorkflowEntity {
        val user = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.WORKFLOW_NOT_FOUND)) }

        if (user.tenant.id != tenantId) {
            throw NotFoundException(Error(ErrorCode.WORKFLOW_NOT_FOUND))
        }
        return user
    }

    @Transactional
    fun save(workflow: WorkflowEntity): WorkflowEntity {
        workflow.modifiedAt = Date()
        return dao.save(workflow)
    }
}
