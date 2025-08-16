package com.wutsi.koki.email.server.service

import com.github.mustachejava.DefaultMustacheFactory
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException
import kotlin.test.Test
import kotlin.test.assertEquals

class EmailTemplateResolverTest {
    private val resolver = EmailTemplateResolver(
        MustacheTemplatingEngine(DefaultMustacheFactory())
    )

    @Test
    fun resolve() {
        val text = resolver.resolve("/email/template.html", mapOf("name" to "Ray"))
        assertEquals(
            """
                Hello world!
                My name is Ray

            """.trimIndent(),
            text,
        )
    }

    @Test
    fun notFound() {
        assertThrows<FileNotFoundException> {
            resolver.resolve("/email/xxx.html", mapOf("name" to "Ray"))
        }
    }
}
