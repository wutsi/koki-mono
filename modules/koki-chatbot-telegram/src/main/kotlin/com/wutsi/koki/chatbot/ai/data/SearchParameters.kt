package com.wutsi.koki.chatbot.ai.data

data class SearchParameters(
    val neighborhood: String? = null,
    val city: String? = null,
    val propertyType: String? = null,
    val minBedrooms: Int? = null,
    val maxBedrooms: Int? = null,
    val leaseType: String? = null,
    val furnishedType: String? = null,
    val valid: Boolean = false,
    val invalidReason: String? = ""
)
