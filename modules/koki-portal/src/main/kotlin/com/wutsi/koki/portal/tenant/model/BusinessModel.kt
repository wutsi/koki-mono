package com.wutsi.koki.portal.tenant.model

import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.JuridictionModel

data class BusinessModel(
    val id: Long = -1,
    val companyName: String = "",
    val phone: String? = null,
    val fax: String? = null,
    val email: String? = null,
    val website: String? = null,
    val address: AddressModel? = null,
    val juridictions: List<JuridictionModel> = emptyList(),
)
