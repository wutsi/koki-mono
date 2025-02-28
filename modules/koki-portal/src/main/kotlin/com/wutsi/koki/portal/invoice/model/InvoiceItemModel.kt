package com.wutsi.koki.portal.invoice.model

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class InvoiceModel(
    val id: Long = -1,
    val orderId: Long? = null,
    val taxId: Long? = null,
    val number: Long = -1,
    val status: InvoiceStatus = InvoiceStatus.UNKNOWN,
    val description: String? = null,
    val customer: CustomerModel = CustomerModel(),

    val subTotalAmount: MoneyModel = MoneyModel(),
    val totalTaxAmount: MoneyModel = MoneyModel(),
    val totalDiscountAmount: MoneyModel = MoneyModel(),
    val totalAmount: MoneyModel = MoneyModel(),
    val amountPaid: MoneyModel = MoneyModel(),
    val amountDue: MoneyModel = MoneyModel(),

    val shippingAddress: AddressModel? = null,
    val billingAddress: AddressModel? = null,

    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val createdBy: UserModel? = null,
    val modifiedBy: UserModel? = null,
    val dueAt: Date? = null,
    val dueAtText: String? = null,
)
