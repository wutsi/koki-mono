package com.wutsi.koki.platform.templating

import com.github.mustachejava.DefaultMustacheFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.trimIndent
import kotlin.to

class MustacheTemplatingEngineTest {
    private val factory = DefaultMustacheFactory()
    private val engine = MustacheTemplatingEngine(factory)

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
