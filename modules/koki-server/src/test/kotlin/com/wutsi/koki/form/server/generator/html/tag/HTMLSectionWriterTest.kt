package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.HTMLImageWriter
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class HTMLImageWriterTest {
    val context = Context()
    val output = StringWriter()
    val writer = HTMLImageWriter()

    val elt = FormElement(
        type = FormElementType.IMAGE,
        url = "https://www.google.com/img/1.png",
        title = "test",
    )

    @Test
    fun write() {
        writer.write(elt, context, output)

        assertEquals(
            "<IMG src='https://www.google.com/img/1.png' alt='test'/>\n",
            output.toString()
        )
    }

    @Test
    fun `null title`() {
        writer.write(elt.copy(title = null), context, output)

        assertEquals(
            "<IMG src='https://www.google.com/img/1.png'/>\n",
            output.toString()
        )
    }

    @Test
    fun `empty title`() {
        writer.write(elt.copy(title = ""), context, output)

        assertEquals(
            "<IMG src='https://www.google.com/img/1.png'/>\n",
            output.toString()
        )
    }

    @Test
    fun `escape title`() {
        writer.write(elt.copy(title = "test with <>"), context, output)

        assertEquals(
            "<IMG src='https://www.google.com/img/1.png' alt='test with &lt;&gt;'/>\n",
            output.toString()
        )
    }
}
