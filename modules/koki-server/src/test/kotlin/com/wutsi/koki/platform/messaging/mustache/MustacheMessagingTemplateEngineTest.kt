package com.wutsi.koki.platform.messaging.mustache

import com.github.mustachejava.DefaultMustacheFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class MustacheMessagingTemplateEngineTest {
    private val factory = DefaultMustacheFactory()
    private val engine = MustacheMessagingTemplateEngine(factory)

    private val data = mapOf(
        "year" to "2025",
        "accountant" to "Yo Man",
        "recipient_name" to "Ray Sponsible"
    )
    private val text = """
                Dear {{recipient_name}},

                The {{year}} tax season has started.
                Make sure to prepare your documents.

                Your beloved accountant
                {{accountant}}
            """.trimIndent()

    @Test
    fun apply() {
        val xtext = engine.apply(text, data)

        assertEquals(
            """
                Dear Ray Sponsible,

                The 2025 tax season has started.
                Make sure to prepare your documents.

                Your beloved accountant
                Yo Man
            """.trimIndent(), xtext
        )
    }
}
