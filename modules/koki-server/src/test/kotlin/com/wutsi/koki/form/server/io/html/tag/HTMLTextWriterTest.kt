package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormAccessControl
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.HTMLTextWriter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class HTMLTextWriterTest {
    val context = Context(
        roleNames = listOf("accountant"),
        data = mapOf("var1" to "value1")
    )
    val output = StringWriter()
    val writer = HTMLTextWriter()

    val elt = FormElement(
        type = FormElementType.TEXT,
        url = "https://www.google.com/img/1.png",
        name = "var1",
        title = "test",
        description = "This is the description",
    )

    @Test
    fun text() {
        writer.write(elt, context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <INPUT name='${elt.name}' value='value1'/>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun `input without value`() {
        writer.write(elt, context.copy(data = emptyMap()), output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <INPUT name='${elt.name}'/>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun number() {
        writer.write(elt.copy(type = FormElementType.NUMBER), context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <INPUT name='${elt.name}' type='number' value='value1'/>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun url() {
        writer.write(elt.copy(type = FormElementType.URL), context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <INPUT name='${elt.name}' type='url' value='value1'/>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun email() {
        writer.write(elt.copy(type = FormElementType.EMAIL), context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <INPUT name='${elt.name}' type='email' value='value1'/>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun date() {
        writer.write(elt.copy(type = FormElementType.DATE), context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <INPUT name='${elt.name}' type='date' value='value1'/>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun time() {
        writer.write(elt.copy(type = FormElementType.TIME), context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <INPUT name='${elt.name}' type='time' value='value1'/>

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
                <INPUT name='${elt.name}' value='value1' required min='1' max='100' minlength='1' maxlength='10' pattern='xxxx'/>

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
                <INPUT name='${elt.name}' value='value1' readonly/>

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
                <INPUT name='${elt.name}' value='value1' readonly/>

            """.trimIndent(),
            output.toString()
        )
    }
}
