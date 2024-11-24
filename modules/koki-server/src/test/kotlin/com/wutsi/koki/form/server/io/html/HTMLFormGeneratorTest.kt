package com.wutsi.koki.form.server.generator.html

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.io.StringWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class HTMLFormGeneratorTest {
    private val provider = mock(HTMLElementWriterProvider::class.java)
    private val elementWriter = mock(HTMLElementWriter::class.java)
    val context = Context(
        provider = provider,
        submitUrl = "https://f.com/submit",
    )
    val writer = StringWriter()
    private val generator = HTMLFormGenerator()

    val form = Form(
        content = FormContent(
            name = "TEST",
            title = "Sample form",
            description = "This is an exempla of form",
            elements = listOf(
                FormElement(type = FormElementType.SECTION),
                FormElement(type = FormElementType.SECTION),
                FormElement(type = FormElementType.SECTION),
            )
        )
    )

    @BeforeEach()
    fun setUp() {
        doReturn(elementWriter).whenever(provider).get(any())
    }

    @Test
    fun generate() {
        generator.generate(form.content, context, writer)

        verify(elementWriter, times(3)).write(any(), eq(context), eq(writer))
        val expected = """
                <DIV class='form test'>
                  <FORM method='post' action='https://f.com/submit'>
                    <DIV class='form-header'>
                      <H1 class='form-title'>Sample form</H1>
                      <DIV class='form-description'>This is an exempla of form</DIV>
                    </DIV>
                    <DIV class='form-body'>
                    </DIV>
                    <DIV class='form-footer'>
                      <DIV class='form-button-group'>
                        <BUTTON type='submit'>Submit</BUTTON>
                      </DIV>
                    </DIV>
                  </FORM>
                </DIV>
            """.trimIndent()
        assertEquals(expected, writer.toString())
    }

    @Test
    fun readOnly() {
        val xcontext = context.copy(readOnly = true)
        generator.generate(form.content, xcontext, writer)

        verify(elementWriter, times(3)).write(any(), eq(xcontext), eq(writer))
        val expected = """
                <DIV class='form test'>
                    <DIV class='form-header'>
                      <H1 class='form-title'>Sample form</H1>
                      <DIV class='form-description'>This is an exempla of form</DIV>
                    </DIV>
                    <DIV class='form-body'>
                    </DIV>
                </DIV>
            """.trimIndent()
        assertEquals(expected, writer.toString())
    }
}
