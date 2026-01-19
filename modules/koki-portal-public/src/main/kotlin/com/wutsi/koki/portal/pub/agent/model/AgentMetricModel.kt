package com.wutsi.koki.portal.pub.agent.model

import com.wutsi.koki.portal.pub.common.model.MoneyModel

data class AgentMetricModel(
    val total: Long = 0,
    val averagePrice: MoneyModel = MoneyModel(),

    @Deprecated("")
    val minPrice: MoneyModel = MoneyModel(),
    @Deprecated("")
    val maxPrice: MoneyModel = MoneyModel(),
    @Deprecated("")
    val totalPrice: MoneyModel = MoneyModel(),
)
