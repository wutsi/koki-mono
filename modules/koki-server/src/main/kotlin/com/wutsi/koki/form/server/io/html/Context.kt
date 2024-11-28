package com.wutsi.koki.form.server.generator.html

data class Context(
    val tenantId: Long = -1,
    val data: Map<String, Any> = emptyMap(),
    val submitUrl: String = "",
    val uploadUrl: String = "",
    val downloadUrl: String = "",
    val provider: HTMLElementWriterProvider = HTMLElementWriterProvider(),
    val roleNames: List<String> = emptyList(),
    val readOnly: Boolean = false,
    val fileResolver: FileResolver = NullFileResolver(),
)
