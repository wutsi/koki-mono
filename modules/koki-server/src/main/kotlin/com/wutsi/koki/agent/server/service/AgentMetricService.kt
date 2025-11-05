package com.wutsi.koki.agent.server.service

import com.wutsi.koki.agent.server.dao.AgentRepository
import com.wutsi.koki.agent.server.domain.AgentEntity
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class AgentService(private val dao: AgentRepository) {
    fun get(id: Long, tenantId: Long): AgentEntity {
        val agent = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.AGENT_NOT_FOUND)) }
        if (agent.tenantId != tenantId){
            throw NotFoundException(Error(ErrorCode.AGENT_NOT_FOUND))
        }
        return agent
    }

    @Transactional
    fun create(userId: Long, tenantId: Long): AgentEntity {
        return dao.save(
            AgentEntity(
                userId = userId,
                tenantId = tenantId
            )
        )
    }

    fun updateRentalMetric(agent: AgentEntity){

    }

    fun updateMetric(agent: AgentEntity) {
        update12mMetric(agent)
        updateOverallMetric(agent)
    }

    private fun update12mMetric(agent: AgentEntity) {

    }

    private fun updateOverallMetric(agent: AgentEntity) {

    }
}
