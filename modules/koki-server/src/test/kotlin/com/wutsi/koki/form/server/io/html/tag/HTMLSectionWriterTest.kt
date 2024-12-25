package com.wutsi.koki.form.server.generator.html.tag

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.form.dto.FormAccessControl
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.HTMLElementWriter
import com.wutsi.koki.form.server.generator.html.HTMLElementWriterProvider
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.io.StringWriter
import kotlin.test.assertEquals

class HTMLSectionWriterTest {
    val output = StringWriter()
    val writer = HTMLSectionWriter()

    private val elementWriter = mock(HTMLElementWriter::class.java)
    private val provider = mock(HTMLElementWriterProvider::class.java)
    val context = Context(
        provider = provider,
        roleNames = listOf("admin")
    )

    val elt = FormElement(
        type = FormElementType.SECTION,
        title = "Personal Information",
        description = "Enter the client personal information",
        accessControl = FormAccessControl(
            viewerRoles = listOf("accountant", "admin"),
            editorRoles = listOf("admin"),
        ),
        elements = listOf(
            FormElement(
                type = FormElementType.IMAGE,
                url = "https://www.google.com/img/1.png",
            ),
            FormElement(
                type = FormElementType.IMAGE,
                url = "https://www.google.com/img/2.png",
                accessControl = FormAccessControl(
                    viewerRoles = listOf("accountant")
                )
            ),
        )
    )

    @BeforeEach()
    fun setUp() {
        doReturn(elementWriter).whenever(provider).get(any())
    }

    @Test
    fun write() {
        writer.write(elt, context, output)

        val expected = """
                <DIV class='section read-only-section'>
                  <DIV class='section-header'>
                    <H2 class='section-title'>Personal Information</H2>
                    <DIV class='section-description'>Enter the client personal information</DIV>
                  </DIV>
                  <DIV class='section-body'>
                    <DIV class='section-item'>
                    </DIV>
                    <DIV class='section-item'>
                    </DIV>
                  </DIV>
                </DIV>

            """.trimIndent()

        val element = argumentCaptor<FormElement>()
        verify(elementWriter, times(2)).write(element.capture(), eq(context), eq(output))

        assertEquals(elt.accessControl?.viewerRoles, element.firstValue.accessControl?.viewerRoles)
        assertEquals(elt.accessControl?.editorRoles, element.firstValue.accessControl?.editorRoles)

        assertEquals(elt.elements?.get(1)?.accessControl?.viewerRoles, element.secondValue.accessControl?.viewerRoles)
        assertEquals(null, element.secondValue.accessControl?.editorRoles)
        assertEquals(expected, output.toString())
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
}
