package com.wutsi.koki.email.server.service

import com.wutsi.koki.platform.templating.TemplatingEngine
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service
import java.io.FileNotFoundException

@Service
class EmailTemplateResolver(private val templateEngine: TemplatingEngine) {
    fun resolve(path: String, data: Map<String, Any>): String {
        val input = EmailTemplateResolver::class.java.getResourceAsStream(path)
        if (input == null) {
            throw FileNotFoundException(path)
        }

        val text = IOUtils.toString(input, "utf-8")
        return templateEngine.apply(text, data)
    }
}
