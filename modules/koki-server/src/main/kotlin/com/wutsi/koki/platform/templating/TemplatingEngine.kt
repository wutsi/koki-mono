package com.wutsi.koki.platform.templating

interface TemplatingEngine {
    fun apply(text: String, data: Map<String, Any>): String
}
