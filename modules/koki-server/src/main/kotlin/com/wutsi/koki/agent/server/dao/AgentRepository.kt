package com.wutsi.koki.agent.server.dao

import com.wutsi.koki.agent.server.domain.AgentEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
class AgentRespository: CrudRepository<AgentEntity, Long>
