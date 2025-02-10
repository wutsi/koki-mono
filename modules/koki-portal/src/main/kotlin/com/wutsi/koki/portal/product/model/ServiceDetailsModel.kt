package com.wutsi.koki.portal.product.model

import com.wutsi.koki.portal.refdata.model.UnitModel

data class ServiceDetailsModel(
    val quantity: Int? = null,
    val unit: UnitModel? = null,
)
