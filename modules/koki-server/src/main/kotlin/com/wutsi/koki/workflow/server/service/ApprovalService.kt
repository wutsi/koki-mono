package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.workflow.server.dao.ApprovalRepository
import com.wutsi.koki.workflow.server.domain.ApprovalEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApprovalService(private val dao: ApprovalRepository) {
    @Transactional
    fun save(entity: ApprovalEntity): ApprovalEntity {
        return dao.save(entity)
    }
}
