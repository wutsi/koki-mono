package com.wutsi.koki.tenant.dto

import com.wutsi.koki.refdata.dto.Address

data class Business(
    val id: Long = -1,
    val tenantId: Long = -1,
    val juridictionId: Long = -1,
    val companyName: String = "",
    val phone: String? = null,
    val fax: String? = null,
    val email: String? = null,
    val website: String? = null,
    val address: Address? = null,
    val taxIdentifiers: List<BusinessTaxIdentifier> = emptyList(),
)
