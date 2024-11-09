package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormAccessControl
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.dto.FormOption
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.HTMLCheckboxesWriter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class HTMLCheckboxesWriterTest {
    val context = Context(
        roleName = "accountant",
        data = mapOf("var1" to "value1")
    )
    val output = StringWriter()
    val writer = HTMLCheckboxesWriter()

    val elt = FormElement(
        type = FormElementType.CHECKBOXES,
        url = "https://www.google.com/img/1.png",
        name = "var1",
        title = "test",
        description = "This is the description",
        options = listOf(
            FormOption(value = "1"),
            FormOption(value = "foo", text = "FOO"),
            FormOption(value = "value1", text = "Value #1"),
        ),
    )

    @Test
    fun checkboxes() {
        writer.write(elt, context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='1'/>
                  <LABEL>1</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='foo'/>
                  <LABEL>FOO</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='value1' checked/>
                  <LABEL>Value #1</LABEL>
                </DIV>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun radio() {
        writer.write(elt.copy(type = FormElementType.MULTIPLE_CHOICE), context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='checkbox' value='1'/>
                  <LABEL>1</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='checkbox' value='foo'/>
                  <LABEL>FOO</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='checkbox' value='value1' checked/>
                  <LABEL>Value #1</LABEL>
                </DIV>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun `other option`() {
        writer.write(
            elt.copy(
                otherOption = FormOption(value = "other-value", text = "This is the other option")
            ),
            context,
            output
        )

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='1'/>
                  <LABEL>1</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='foo'/>
                  <LABEL>FOO</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='value1' checked/>
                  <LABEL>Value #1</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='text' value='other-value'/>
                  <LABEL>This is the other option</LABEL>
                </DIV>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun `other option value`() {
        writer.write(
            elt.copy(
                otherOption = FormOption(value = "other-value", text = "This is the other option")
            ),
            context.copy(
                data = mapOf(
                    "var1" to "other-value",
                    "var1_other" to "This is the other text",
                )
            ),
            output
        )

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='1'/>
                  <LABEL>1</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='foo'/>
                  <LABEL>FOO</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='value1'/>
                  <LABEL>Value #1</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='text' value='other-value' checked/>
                  <LABEL>This is the other option</LABEL>
                </DIV>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun `no value`() {
        writer.write(
            elt.copy(
                otherOption = FormOption(value = "other-value", text = "This is the other option")
            ),
            context.copy(
                data = emptyMap()
            ),
            output
        )

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='1'/>
                  <LABEL>1</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='foo'/>
                  <LABEL>FOO</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='value1'/>
                  <LABEL>Value #1</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='text' value='other-value'/>
                  <LABEL>This is the other option</LABEL>
                </DIV>

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
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='1' readonly/>
                  <LABEL>1</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='foo' readonly/>
                  <LABEL>FOO</LABEL>
                </DIV>
                <DIV class='item'>
                  <INPUT name='var1' type='radio' value='value1' readonly checked/>
                  <LABEL>Value #1</LABEL>
                </DIV>

            """.trimIndent(),
            output.toString()
        )
    }
}
