package com.wutsi.koki.form.server.generator.html.tag

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.form.dto.FormAccessControl
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.HTMLVideoWriter
import com.wutsi.koki.form.server.generator.html.video.VideoEmbedder
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class HTMLVideoWriterTest {
    val embedder1 = mock<VideoEmbedder>()
    val embedder2 = mock<VideoEmbedder>()
    val embedder3 = mock<VideoEmbedder>()

    val context = Context(
        roleNames = listOf("accountant"),
    )
    val output = StringWriter()
    val writer = HTMLVideoWriter(listOf(embedder1, embedder2, embedder3))

    val embedUrl = "https://www.youtube.com/embed/l9lLQLckJn4"
    val elt = FormElement(
        type = FormElementType.VIDEO,
        url = "https://www.youtube.com/watch?v=l9lLQLckJn4",
    )

    @BeforeEach
    fun setUp() {
        doReturn(null).whenever(embedder1).embedUrl(any())
        doReturn(embedUrl).whenever(embedder2).embedUrl(any())
        doReturn(null).whenever(embedder3).embedUrl(any())
    }

    @Test
    fun write() {
        writer.write(elt, context, output)
        assertEquals("<IFRAME src='$embedUrl'></IFRAME>\n", output.toString())
    }

    @Test
    fun `bad URL`() {
        doReturn(null).whenever(embedder1).embedUrl(any())
        doReturn(null).whenever(embedder2).embedUrl(any())
        doReturn(null).whenever(embedder3).embedUrl(any())

        writer.write(elt, context, output)
        assertTrue(output.toString().isEmpty())
    }

    @Test
    fun `null URL`() {
        doReturn(null).whenever(embedder1).embedUrl(any())
        doReturn(null).whenever(embedder2).embedUrl(any())
        doReturn(null).whenever(embedder3).embedUrl(any())

        writer.write(elt.copy(url = null), context, output)
        assertTrue(output.toString().isEmpty())
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
