package com.wutsi.koki.portal.tax.model

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.portal.product.model.ProductModel

data class TaxProductModel(
    val id: Long = -1,
    val taxId: Long = -1,
    val unitPriceId: Long = -1,
    val product: ProductModel = ProductModel(),
    val description: String? = null,
    val quantity: Int = 1,
    val unitPrice: MoneyModel = MoneyModel(),
    val subTotal: MoneyModel = MoneyModel(),
)
