package com.wutsi.koki.portal.rest

import org.springframework.web.client.RestTemplate

class KokiForms(
    private val baseUrl: String,
    private val rest: RestTemplate,
) {
    fun html(
        formId: String,
        submitUrl: String?,
        activityInstanceId: String? = null,
        roleName: String? = null,
        tenantId: Long
    ): String {
        val prefix = "$baseUrl/v1/forms/html/${tenantId}.${formId}.html"
        val suffix = listOf(
            activityInstanceId?.let { "aiid=$it" },
            submitUrl?.let { "submit-url=$it" },
            roleName?.let { "role-name=$it" },
        )
            .filterNotNull()
            .joinToString(separator = "&")
            .ifEmpty { null }

        val url = listOf(prefix, suffix).filterNotNull().joinToString(separator = "?")
        return rest.getForEntity(url, String::class.java).body
    }
}
