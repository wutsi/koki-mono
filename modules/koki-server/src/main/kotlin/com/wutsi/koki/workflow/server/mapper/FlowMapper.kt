package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.workflow.dto.Flow
import com.wutsi.koki.workflow.server.domain.FlowEntity
import org.springframework.stereotype.Service

@Service
class FlowMapper {
    fun toFlow(entity: FlowEntity): Flow {
        return Flow(
            id = entity.id!!,
            fromId = entity.from.id!!,
            toId = entity.to.id!!,
            expression = entity.expression
        )
    }
}
