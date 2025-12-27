package com.wutsi.koki.portal.pub.agent.model

import com.wutsi.koki.portal.pub.common.model.MoneyModel
import com.wutsi.koki.portal.pub.common.util.MoneyUtil
import com.wutsi.koki.portal.pub.user.model.UserModel

data class AgentModel(
    val id: Long = -1,
    val user: UserModel = UserModel(),
    val totalSales: Long? = null,
    val totalRentals: Long? = null,
    val past12mSales: Long? = null,
    val past12mRentals: Long? = null,
    val totalSalesMetric: AgentMetricModel? = null,
    val past12mSalesMetric: AgentMetricModel? = null,
    val totalRentalsMetric: AgentMetricModel? = null,
    val past12mRentalsMetric: AgentMetricModel? = null,
    val publicUrl: String = "",
) {
    fun priceRangeText(min: MoneyModel, max: MoneyModel): String {
        return MoneyUtil.priceRangeText(min, max)
    }
}
