package com.wutsi.koki.email.server.service.filter

import kotlin.test.Test
import kotlin.test.assertEquals

class CssFilterTest {
    private val filter = CssFilter()

    @Test
    fun filter() {
        val html = """
            <h1>Hello</b>
            <div class="margin-top">Hello</div>
            <button class="btn btn-primary">Yo</button>
        """.trimIndent()

        val result = filter.filter(html, 1)

        assertEquals(
            """
                <h1>
                  Hello
                  <div class="margin-top" style="margin-top: 16px;">Hello</div>
                  <button class="btn btn-primary" style="border-radius: 16px;display: inline-block;font-weight: 400;color: #FFFFFF;background-color: #1D7EDF;text-align: center;vertical-align: middle;border: 1px solid transparent;padding: .375rem .75rem;font-size: 1rem;line-height: 1.5;text-decoration: none;">Yo</button>
                </h1>
            """.trimIndent(),
            result.trimIndent(),
        )
    }

    @Test
    fun `no class`() {
        val html = """
            <h1>Hello</b>
            <div>Hello</div>
            <button>Yo</button>
        """.trimIndent()

        val result = filter.filter(html, 1)

        assertEquals(html, result)
    }
}
