package com.wutsi.koki.portal.product.model

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.portal.tenant.model.TypeModel
import java.util.Date

data class PriceModel(
    val id: Long = -1,
    val accountType: TypeModel? = null,
    val name: String? = null,
    val amount: MoneyModel = MoneyModel(),
    val active: Boolean = true,
    val startAt: Date? = null,
    val startAtText: String? = null,
    val endAt: Date? = null,
    val endAtText: String? = null,
)
