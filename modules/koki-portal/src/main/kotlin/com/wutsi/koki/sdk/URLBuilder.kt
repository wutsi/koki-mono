package com.wutsi.koki.sdk

import java.net.URLEncoder

class URLBuilder(private val baseUrl: String) {
    fun build(
        path: String,
        parameters: Map<String, Any?> = emptyMap()
    ): String {
        val address = "${baseUrl}$path"
        val queryString = parameters
            .filter { parameter -> parameter.value != null }
            .map { parameter ->
                val value = parameter.value
                if (value is Collection<*>) {
                    value.map { "${parameter.key}=" + URLEncoder.encode(it.toString(), "utf-8") }
                        .joinToString(separator = "&")
                } else {
                    "${parameter.key}=" + URLEncoder.encode(parameter.value.toString(), "utf-8")
                }
            }
            .joinToString(separator = "&")
            .ifEmpty { null }

        return listOf(address, queryString)
            .filterNotNull()
            .joinToString(separator = "?")
    }
}
