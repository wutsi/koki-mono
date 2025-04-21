package com.wutsi.koki.portal.client.invoice.model

import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.portal.client.common.model.MoneyModel
import java.util.Date

data class InvoiceModel(
    val id: Long = -1,
    val paynowId: String = "",
    val number: Long = -1,
    val status: InvoiceStatus = InvoiceStatus.UNKNOWN,
    val totalAmount: MoneyModel = MoneyModel(),
    val amountPaid: MoneyModel = MoneyModel(),
    val amountDue: MoneyModel = MoneyModel(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val downloadUrl: String = "",
    val paynowUrl: String? = null
)
