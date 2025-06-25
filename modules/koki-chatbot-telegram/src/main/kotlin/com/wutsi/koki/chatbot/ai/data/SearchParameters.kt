package com.wutsi.koki.chatbot.ai.data

data class PropertyData(
    val url: String = "",
    val pricePerMonth: Double? = null,
    val pricePerNight: Double? = null,
    val currency: String = "",
    val city: String? = null,
    val neighborhood: String? = null,
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val area: Int? = null,
    val title: String? = null,
    val summary: String? = null,
)
