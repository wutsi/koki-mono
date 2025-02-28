package com.wutsi.koki.portal.invoice.model

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.portal.product.model.ProductModel
import com.wutsi.koki.portal.refdata.model.UnitModel

data class InvoiceItemModel(
    val id: Long = -1,
    val productId: ProductModel = ProductModel(),
    val unitPriceId: Long = -1,
    val unitId: UnitModel? = null,
    val description: String? = null,
    val quantity: Int = 1,
    val unitPrice: MoneyModel = MoneyModel(),
    val subTotal: MoneyModel = MoneyModel(),
//    val taxes: List<InvoiceSalesTax> = emptyList(),
)
