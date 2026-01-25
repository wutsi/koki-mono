package com.wutsi.koki.agent.dto.event

data class AgentCreatedEvent(
    val agentId: Long = -1,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
