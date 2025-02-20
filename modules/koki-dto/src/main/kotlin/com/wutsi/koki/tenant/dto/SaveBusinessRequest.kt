package com.wutsi.koki.tenant.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import java.util.Date

data class SaveBusinessRequest(
    val tenantId: Long = -1,
    val juridictionId: Long = -1,

    @get:Size(max = 100) val companyName: String = "",
    @get:Size(max = 30) val registrationNumber: String? = null,
    val dateOfRegistration: Date? = null,

    @get:Size(max = 30) val phone: String? = null,
    @get:Size(max = 30) val fax: String? = null,
    @get:Email @get:Size(max = 255) val email: String? = null,
    val website: String? = null,

    @get:Size(max = 30) val addressPostalCode: String? = null,
    @get:Size(max = 2) val addressCountry: String? = null,
    val addressStreet: String? = null,
    val addressCityId: Long? = null,

    val taxIdentifiers: Map<String, String> = emptyMap(),
)
