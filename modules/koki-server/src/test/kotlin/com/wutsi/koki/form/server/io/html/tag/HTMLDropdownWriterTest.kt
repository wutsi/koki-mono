package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormAccessControl
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.dto.FormOption
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.HTMLDropdownWriter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class HTMLDropdownWriterTest {
    val context = Context(
        roleNames = listOf("accountant"),
        data = mapOf("var1" to "value1")
    )
    val output = StringWriter()
    val writer = HTMLDropdownWriter()

    val elt = FormElement(
        type = FormElementType.DROPDOWN,
        url = "https://www.google.com/img/1.png",
        name = "var1",
        title = "test",
        description = "This is the description",
        options = listOf(
            FormOption(value = "1"),
            FormOption(value = "foo", text = "FOO"),
            FormOption(value = "value1", text = "Value #1"),
        )
    )

    @Test
    fun dropdown() {
        writer.write(elt, context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <SELECT name='${elt.name}' value='value1'>
                  <OPTION value='1'>1</OPTION>
                  <OPTION value='foo'>FOO</OPTION>
                  <OPTION value='value1' selected>Value #1</OPTION>
                </SELECT>

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
                <SELECT name='${elt.name}'>
                  <OPTION value='1'>1</OPTION>
                  <OPTION value='foo'>FOO</OPTION>
                  <OPTION value='value1'>Value #1</OPTION>
                </SELECT>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun `validation rules`() {
        val xelt = elt.copy(
            required = true,
        )

        writer.write(xelt, context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN><SPAN class='required'>*</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <SELECT name='${elt.name}' value='value1' required>
                  <OPTION value='1'>1</OPTION>
                  <OPTION value='foo'>FOO</OPTION>
                  <OPTION value='value1' selected>Value #1</OPTION>
                </SELECT>

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
                <SELECT name='${elt.name}' value='value1' readonly>
                  <OPTION value='1' disabled>1</OPTION>
                  <OPTION value='foo' disabled>FOO</OPTION>
                  <OPTION value='value1' selected>Value #1</OPTION>
                </SELECT>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun readOnly() {
        val xelt = elt.copy(readOnly = true)

        writer.write(xelt, context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <SELECT name='${elt.name}' value='value1' readonly>
                  <OPTION value='1' disabled>1</OPTION>
                  <OPTION value='foo' disabled>FOO</OPTION>
                  <OPTION value='value1' selected>Value #1</OPTION>
                </SELECT>

            """.trimIndent(),
            output.toString()
        )
    }
}
