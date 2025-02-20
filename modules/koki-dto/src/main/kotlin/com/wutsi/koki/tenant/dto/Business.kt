package com.wutsi.koki.tenant.dto

import java.util.Date

data class Business(
    val id: Long = -1,
    val tenantId: Long = -1,
    val juridictionId: Long = -1,
    val companyName: String = "",
    val registrationNumber: String? = null,
    val dateOfRegistration: Date? = null,
    val phone: String? = null,
    val fax: String? = null,
    val email: String? = null,
    val website: String? = null,
    val address: String? = null,
    val taxIdentifiers: List<BusinessTaxIdentifier> = emptyList(),
)
