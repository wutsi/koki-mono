package com.wutsi.koki.portal.pub.agent.model

import com.wutsi.koki.portal.pub.user.model.UserModel

data class AgentModel(
    val id: Long = -1,
    val user: UserModel = UserModel(),
    val activeSaleMetric: AgentMetricModel? = null,
    val activeRentalMetric: AgentMetricModel? = null,
    val publicUrl: String = "",
)
