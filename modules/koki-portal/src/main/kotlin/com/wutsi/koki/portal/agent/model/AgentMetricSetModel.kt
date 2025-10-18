package com.wutsi.koki.portal.agent.model

data class AgentMetricSetModel(
    val rentals: AgentMetricModel = AgentMetricModel(),
    val sales: AgentMetricModel = AgentMetricModel(),
)
