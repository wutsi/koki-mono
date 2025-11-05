package com.wutsi.koki.agent.dto

data class SearchAgentResponse(
    val agents: List<AgentSummary> = emptyList()
)
