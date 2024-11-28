package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormAccessControl
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.File
import com.wutsi.koki.form.server.generator.html.FileResolver
import com.wutsi.koki.form.server.generator.html.HTMLFileUploadWriter
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class HTMLFileUploadWriterTest : FileResolver {
    var file: File? = File(
        name = "foo.txt",
        contentLength = 100000,
        contentType = "text/plain"
    )

    val context = Context(
        roleNames = listOf("accountant"),
        data = mapOf("var1" to "11111"),
        fileResolver = this,
        downloadUrl = "https://foo.com/storage/download",
        uploadUrl = "https://foo.com/storage/upload"
    )
    val output = StringWriter()
    val writer = HTMLFileUploadWriter()

    val elt = FormElement(
        type = FormElementType.FILE_UPLOAD,
        url = "https://www.google.com/img/1.png",
        name = "var1",
        title = "test",
        description = "This is the description",
    )

    override fun resolve(id: String, tenantId: Long): File? {
        return file
    }

    @Test
    fun file() {
        writer.write(elt, context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <DIV class='file-upload-container'>
                  <INPUT type='hidden' name='var1' value='11111'/>
                  <BUTTON type='button' class='btn-upload' rel='var1'>Upload File</BUTTON>
                  <INPUT type='file' name='var1-file' rel='var1' data-upload-url='https://foo.com/storage/upload'/>
                  <SPAN data-name='var1-filename'>
                    <A class='filename' href='https://foo.com/storage/download/11111/foo.txt'>foo.txt</A>
                    <button class='btn-close' type='button' name='var1-close' rel='var1'></button>
                  </SPAN>
                </DIV>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun required() {
        writer.write(elt.copy(required = true), context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN><SPAN class='required'>*</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <DIV class='file-upload-container'>
                  <INPUT type='hidden' name='var1' value='11111' required/>
                  <BUTTON type='button' class='btn-upload' rel='var1'>Upload File</BUTTON>
                  <INPUT type='file' name='var1-file' rel='var1' data-upload-url='https://foo.com/storage/upload'/>
                  <SPAN data-name='var1-filename'>
                    <A class='filename' href='https://foo.com/storage/download/11111/foo.txt'>foo.txt</A>
                    <button class='btn-close' type='button' name='var1-close' rel='var1'></button>
                  </SPAN>
                </DIV>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun `read only`() {
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
                <DIV class='file-upload-container'>
                  <INPUT type='hidden' name='var1' value='11111'/>
                  <SPAN data-name='var1-filename'>
                    <A class='filename' href='https://foo.com/storage/download/11111/foo.txt'>foo.txt</A>
                  </SPAN>
                </DIV>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun `file not found`() {
        file = null

        writer.write(elt, context, output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <DIV class='file-upload-container'>
                  <INPUT type='hidden' name='var1' value='11111'/>
                  <BUTTON type='button' class='btn-upload' rel='var1'>Upload File</BUTTON>
                  <INPUT type='file' name='var1-file' rel='var1' data-upload-url='https://foo.com/storage/upload'/>
                  <SPAN data-name='var1-filename'>
                  </SPAN>
                </DIV>

            """.trimIndent(),
            output.toString()
        )
    }

    @Test
    fun `no value`() {
        file = null

        writer.write(elt, context.copy(data = emptyMap()), output)

        assertEquals(
            """
                <LABEL class='title'><SPAN>test</SPAN></LABEL>
                <DIV class='description'>This is the description</DIV>
                <DIV class='file-upload-container'>
                  <INPUT type='hidden' name='var1'/>
                  <BUTTON type='button' class='btn-upload' rel='var1'>Upload File</BUTTON>
                  <INPUT type='file' name='var1-file' rel='var1' data-upload-url='https://foo.com/storage/upload'/>
                  <SPAN data-name='var1-filename'>
                  </SPAN>
                </DIV>

            """.trimIndent(),
            output.toString()
        )
    }
}
