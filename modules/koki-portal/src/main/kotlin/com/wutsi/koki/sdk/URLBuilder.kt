package com.wutsi.koki.sdk

import java.net.URLEncoder

class URLBuilder(private val baseUrl: String) {
    fun build(
        path: String,
        parameters: Map<String, Any?> = emptyMap()
    ): String {
        val address = "${baseUrl}$path"
        val queryString = parameters
            .mapNotNull { parameter ->
                val value = parameter.value
                if (value is Collection<*>) {
                    if (value.isNotEmpty()) {
                        value.map { "${parameter.key}=" + URLEncoder.encode(it.toString(), "utf-8") }
                            .joinToString(separator = "&")
                    } else {
                        null
                    }
                } else if (value != null) {
                    "${parameter.key}=" + URLEncoder.encode(parameter.value.toString(), "utf-8")
                } else {
                    null
                }
            }
            .joinToString(separator = "&")
            .ifEmpty { null }

        return listOf(address, queryString)
            .filterNotNull()
            .joinToString(separator = "?")
    }
}
