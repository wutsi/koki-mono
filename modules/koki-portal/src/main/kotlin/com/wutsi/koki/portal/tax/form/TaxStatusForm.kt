package com.wutsi.koki.portal.tax.form

import com.wutsi.koki.tax.dto.TaxStatus

data class TaxStatusForm(
    val status: TaxStatus,
    val formId: Long? = null
)
