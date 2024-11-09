package com.wutsi.koki.form.server.generator.html

data class Context(
    val language: String = "en",
    val roleName: String? = null,
    val data: Map<String, String> = emptyMap(),
    val provider: HTMLElementWriterProvider = HTMLElementWriterProvider(),
    val submitUrl: String = "",
    val successUrl: String? = null,
    val errorUrl: String? = null,
)
