package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.server.dao.FlowRepository
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.FlowEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FlowService(private val dao: FlowRepository) {
    fun get(from: ActivityEntity, to: ActivityEntity): FlowEntity {
        return dao.findByFromAndTo(from, to)
            ?: throw NotFoundException(
                error = Error(
                    code = ErrorCode.WORKFLOW_FLOW_NOT_FOUND,
                    data = mapOf(
                        "from" to from.name,
                        "to" to from.name,
                    )
                )
            )
    }

    @Transactional
    fun save(flow: FlowEntity): FlowEntity {
        return dao.save(flow)
    }

    @Transactional
    fun deleteAll(flows: List<FlowEntity>) {
        dao.deleteAll(flows)
    }
}
