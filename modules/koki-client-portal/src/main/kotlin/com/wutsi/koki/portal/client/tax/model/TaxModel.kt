package com.wutsi.koki.portal.client.tax.model

import com.wutsi.koki.portal.client.account.model.AccountModel
import com.wutsi.koki.portal.client.tenant.model.TypeModel
import com.wutsi.koki.tax.dto.TaxStatus
import java.util.Date

data class TaxModel(
    val id: Long = -1,
    val fiscalYear: Int = -1,
    val status: TaxStatus = TaxStatus.NEW,
    val description: String? = null,
    val taxType: TypeModel? = null,
    val account: AccountModel = AccountModel(),
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
    val startAt: Date? = null,
    val startAtText: String? = null,
) {
    val name: String
        get() = (taxType?.let { type -> "$fiscalYear - ${type.title}" } ?: fiscalYear.toString())
}
