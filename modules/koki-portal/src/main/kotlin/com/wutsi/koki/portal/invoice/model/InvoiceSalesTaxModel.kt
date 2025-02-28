package com.wutsi.koki.portal.invoice.model

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.portal.refdata.model.SalesTaxModel

data class InvoiceSalesTaxModel(
    val id: Long = -1,
    val salesTax: SalesTaxModel = SalesTaxModel(),
    val rate: Double = 0.0,
    val amount: MoneyModel = MoneyModel(),
)
