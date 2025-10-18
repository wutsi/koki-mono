package com.wutsi.koki.portal.agent.model

import com.wutsi.koki.portal.user.model.UserModel

data class AgentModel(
    val id: Long = -1,
    val user: UserModel = UserModel(),
    val metrics: AgentMetricSetModel = AgentMetricSetModel(),
    val pyMetrics: AgentMetricSetModel = AgentMetricSetModel(),
)
