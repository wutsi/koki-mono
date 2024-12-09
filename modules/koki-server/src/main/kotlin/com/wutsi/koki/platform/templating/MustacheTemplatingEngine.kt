package com.wutsi.koki.platform.messaging.mustache

import com.github.mustachejava.MustacheFactory
import com.wutsi.koki.platform.messaging.MessagingTemplateEngine
import java.io.StringReader
import java.io.StringWriter

class MustacheMessagingTemplateEngine(private val factory: MustacheFactory) : MessagingTemplateEngine {
    override fun apply(text: String, data: Map<String, Any>): String {
        val reader = StringReader(text)
        val writer = StringWriter()
        factory.compile(reader, "text")
            .execute(writer, data)
        return writer.toString()
    }
}
