package com.wutsi.koki.portal.tax.model

import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.tenant.model.TypeModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.tax.dto.TaxStatus
import java.util.Date

data class TaxModel(
    val id: Long = -1,
    val invoiceId: Long? = null,
    val fiscalYear: Int = -1,
    val status: TaxStatus = TaxStatus.NEW,
    val description: String? = null,
    val taxType: TypeModel? = null,
    val account: AccountModel = AccountModel(),
    val accountant: UserModel? = null,
    val technician: UserModel? = null,
    val assignee: UserModel? = null,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
    val startAt: Date? = null,
    val startAtText: String? = null,
    val dueAt: Date? = null,
    val dueAtText: String? = null,
    val createdBy: UserModel? = null,
    val modifiedBy: UserModel? = null,
) {
    val name: String
        get() = (taxType?.let { type -> "$fiscalYear - ${type.title}" } ?: fiscalYear.toString())
}
