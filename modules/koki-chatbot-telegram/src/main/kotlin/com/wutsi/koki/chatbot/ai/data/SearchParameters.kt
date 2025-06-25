package com.wutsi.koki.chatbot.ai.data

data class SearchParameters(
    val neighborhoodId: Long? = null,
    val neighborhood: String? = null,
    val cityId: Long? = null,
    val city: String? = null,
    val propertyType: String? = null,
    val minBedrooms: Int? = null,
    val maxBedrooms: Int? = null,
    val leaseType: String? = null,
    val furnishedType: String? = null,
)
