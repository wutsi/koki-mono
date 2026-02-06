package com.wutsi.koki.portal.pub.agent.model

import com.wutsi.koki.portal.pub.common.model.MoneyModel

data class AgentMetricModel(
    val total: Long = 0,
    val averagePrice: MoneyModel = MoneyModel(),
)
