package com.wutsi.koki.portal.tax.form

data class TaxForm(
    val fiscalYear: Int = -1,
    val description: String? = null,
    val taxTypeId: Long? = null,
    val accountId: Long = -1,
    val accountantId: Long? = null,
    val technicianId: Long? = null,
    val assigneeId: Long? = null,
    val startAt: String = "",
    val dueAt: String = "",
)
