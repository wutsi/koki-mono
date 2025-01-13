package com.wutsi.koki.portal.tax.form

import com.wutsi.koki.tax.dto.TaxStatus

data class TaxStatusForm(
    val assigneeId: Long? = null,
    val status: TaxStatus = TaxStatus.NEW,
    val notes: String = "",
)
