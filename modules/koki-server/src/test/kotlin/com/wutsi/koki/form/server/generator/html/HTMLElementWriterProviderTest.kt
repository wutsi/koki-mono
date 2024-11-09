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
import org.mockito.Mockito.mock
import java.io.StringWriter
import kotlin.test.Test

class HTMLFormGeneratorTest {
    private val provider = mock(HTMLElementWriterProvider::class.java)
    private val elementWriter = mock(HTMLElementWriter::class.java)
    val context = Context(
        provider = provider
    )
    val writer = StringWriter()
    private val generator = HTMLFormGenerator()

    @Test
    fun generate() {
        doReturn(elementWriter).whenever(provider).get(any())

        val form = Form(
            content = FormContent(
                sections = listOf(
                    FormElement(type = FormElementType.SECTION),
                    FormElement(type = FormElementType.SECTION),
                    FormElement(type = FormElementType.SECTION),
                )
            )
        )

        generator.generate(form, context, writer)

        verify(elementWriter, times(3)).write(any(), eq(context), eq(writer))
    }
}
