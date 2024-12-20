package com.wutsi.koki.portal.model

import com.wutsi.koki.service.dto.AuthorizationType
import java.util.Date

data class ServiceModel(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val baseUrl: String = "",
    val authorizationType: AuthorizationType = AuthorizationType.UNKNOWN,
    val username: String? = null,
    val password: String? = null,
    val apiKey: String? = null,
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
) {
    val longTitle: String
        get() = if (title.isEmpty()) {
            name
        } else {
            "$name - $title"
        }

    val url: String
        get() = "/settings/services/$id"
}
