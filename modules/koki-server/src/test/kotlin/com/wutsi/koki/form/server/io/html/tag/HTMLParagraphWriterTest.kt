package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormAccessControl
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.HTMLParagraphWriter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class HTMLParagraphWriterTest {
    val context = Context(
        roleName = "accountant",
        data = mapOf("var1" to "value1")
    )
    val output = StringWriter()
    val writer = HTMLParagraphWriter()

    val elt = FormElement(
        type = FormElementType.PARAGRAPH,
        name = "var1",
        title = "test",
        description = "This is the description",
    )

    @Test
    fun paragraph() {
        writer.write(elt, context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <TEXTAREA name='${elt.name}'>value1</TEXTAREA>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun `paragraph without value`() {
        writer.write(elt, context.copy(data = emptyMap()), output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <TEXTAREA name='${elt.name}'></TEXTAREA>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun `validation rules`() {
        val xelt = elt.copy(
            min = "1",
            max = "100",
            minLength = 1,
            maxLength = 10,
            required = true,
            pattern = "xxxx"
        )

        writer.write(xelt, context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN><SPAN class='required'>*</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <TEXTAREA name='${elt.name}' required min='1' max='100' minlength='1' maxlength='10' pattern='xxxx'>value1</TEXTAREA>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun `not viewer`() {
        val xelt = elt.copy(
            accessControl = FormAccessControl(
                viewerRoles = listOf("X", "Y", "Z")
            )
        )

        writer.write(xelt, context, output)

        assertTrue(output.toString().isEmpty())
    }

    @Test
    fun `not editor`() {
        val xelt = elt.copy(
            accessControl = FormAccessControl(
                editorRoles = listOf("X", "Y", "Z")
            )
        )

        writer.write(xelt, context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <TEXTAREA name='${elt.name}' readonly>value1</TEXTAREA>

            """.trimIndent(),
            output.toString()
        )
    }
}
