package com.wutsi.koki.form.server.generator.html

data class Context(
    val roleName: String? = null,
    val data: Map<String, Any> = emptyMap(),
    val provider: HTMLElementWriterProvider = HTMLElementWriterProvider(),
    val submitUrl: String = "",
)
