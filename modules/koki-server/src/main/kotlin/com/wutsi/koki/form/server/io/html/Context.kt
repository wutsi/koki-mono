package com.wutsi.koki.form.server.generator.html

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.server.service.FormLogicEvaluator
import com.wutsi.koki.platform.expression.ExpressionEvaluator

data class Context(
    val tenantId: Long = -1,
    val data: Map<String, Any> = emptyMap(),
    val submitUrl: String = "",
    val uploadUrl: String = "",
    val downloadUrl: String = "",
    val provider: HTMLElementWriterProvider = HTMLElementWriterProvider(),
    val roleNames: List<String> = emptyList(),
    val readOnly: Boolean = false,
    val preview: Boolean = false,
    val fileResolver: FileResolver = NullFileResolver(),
    val formLogicEvaluator: FormLogicEvaluator = FormLogicEvaluator(ObjectMapper(), ExpressionEvaluator()),
)
