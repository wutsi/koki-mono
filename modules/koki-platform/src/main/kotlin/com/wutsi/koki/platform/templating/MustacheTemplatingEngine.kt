package com.wutsi.koki.platform.templating

import com.github.mustachejava.MustacheFactory
import java.io.StringReader
import java.io.StringWriter

class MustacheTemplatingEngine(private val factory: MustacheFactory) : TemplatingEngine {
    override fun apply(text: String, data: Map<String, Any>): String {
        val reader = StringReader(text)
        val writer = StringWriter()
        factory.compile(reader, "text")
            .execute(writer, data)
        return writer.toString()
    }
}
