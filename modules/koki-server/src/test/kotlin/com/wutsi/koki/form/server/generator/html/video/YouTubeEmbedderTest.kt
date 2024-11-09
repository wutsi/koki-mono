package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormAccessControl
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.HTMLSectionWriter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class HTMLSectionWriterTest {
    val context = Context()
    val output = StringWriter()
    val writer = HTMLSectionWriter()

    val elt = FormElement(
        type = FormElementType.SECTION,
        title = "Personal Information",
        description = "Enter the client personal information",
        elements = listOf(
            FormElement(
                type = FormElementType.IMAGE,
                url = "https://www.google.com/img/1.png",
            ),
            FormElement(
                type = FormElementType.IMAGE,
                url = "https://www.google.com/img/2.png",
            ),
        )
    )

    @Test
    fun write() {
        writer.write(elt, context, output)

        val expected = """
                <DIV class='section'>
                  <H2 class='section-title'>Personal Information</H2>
                  <DIV class='section-description'>Enter the client personal information</DIV>
                  <DIV class='section-item'>
                    <IMG src='https://www.google.com/img/1.png'/>
                  </DIV>
                  <DIV class='section-item'>
                    <IMG src='https://www.google.com/img/2.png'/>
                  </DIV>
                </DIV>

            """.trimIndent()
        assertEquals(expected, output.toString())
    }

    @Test
    fun `not viewer`() {
        val elt = FormElement(
            type = FormElementType.IMAGE,
            url = "https://www.google.com/img/1.png",
            accessControl = FormAccessControl(
                viewerRoles = listOf("X", "Y", "Z")
            )
        )

        writer.write(elt, context, output)

        assertTrue(output.toString().isEmpty())
    }
}
