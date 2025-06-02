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
                <html>
                  <head></head>
                  <body>
                    <h1>
                      Hello
                      <div class="margin-top" style="margin-top: 16px;">Hello</div>
                      <button class="btn btn-primary" style="border-radius: 4px;padding: 4px 8px;display: inline-block;font-weight: 400;text-align: center;vertical-align: middle;border: 1px solid transparent;font-size: 1rem;line-height: 1.5;text-decoration: none;;color: #FFFFFF;background-color: #1D7EDF;border: 1px solid transparent;">Yo</button>
                    </h1>
                  </body>
                </html>
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
