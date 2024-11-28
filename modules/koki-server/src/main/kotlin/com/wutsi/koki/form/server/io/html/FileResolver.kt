package com.wutsi.koki.form.server.generator.html

data class Context(
    val tenantId: Long = -1,
    val data: Map<String, Any> = emptyMap(),
    val provider: HTMLElementWriterProvider = HTMLElementWriterProvider(),
    val submitUrl: String = "",
    val fileDownloadUrl: String = "",
    val roleNames: List<String> = emptyList(),
    val readOnly: Boolean = false,
)