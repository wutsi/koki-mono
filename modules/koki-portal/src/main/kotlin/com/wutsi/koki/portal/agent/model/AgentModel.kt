package com.wutsi.koki.portal.agent.model

import com.wutsi.koki.platform.util.NumberUtils
import com.wutsi.koki.portal.common.model.MoneyModel
import com.wutsi.koki.portal.user.model.UserModel

data class AgentModel(
    val id: Long = -1,
    val user: UserModel = UserModel(),
    val totalSales: Long? = null,
    val totalRentals: Long? = null,
    val past12mSales: Long? = null,
    val past12mRentals: Long? = null,

    @Deprecated("")
    val metrics: AgentMetricSetModel = AgentMetricSetModel(),

    @Deprecated("")
    val pyMetrics: AgentMetricSetModel = AgentMetricSetModel(),

    val totalSalesMetric: AgentMetricModel? = null,
    val past12mSalesMetric: AgentMetricModel? = null,
    val totalRentalsMetric: AgentMetricModel? = null,
    val past12mRentalsMetric: AgentMetricModel? = null,
) {
    fun priceRangeText(min: MoneyModel, max: MoneyModel): String {
        if (min.amount == max.amount) {
            return min.shortText
        } else {
            val currency = min.shortText.split(" ").firstOrNull() ?: ""
            return currency + " " +
                NumberUtils.shortText(min.amount.toLong()) + " - " +
                NumberUtils.shortText(max.amount.toLong())
        }
    }
}
