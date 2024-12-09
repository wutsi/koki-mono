package com.wutsi.koki.platform.messaging

interface MessagingTemplateEngine {
    fun apply(text: String, data: Map<String, Any>): String
}
