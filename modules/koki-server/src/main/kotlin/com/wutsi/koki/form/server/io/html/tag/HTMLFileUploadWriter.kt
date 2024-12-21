package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import org.apache.commons.io.FilenameUtils
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

class HTMLFileUploadWriter : AbstractHTMLImputElementWriter() {
    override fun doWriteInput(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        val value = context.data[element.name]?.toString()

        writer.write("<DIV class='file-upload-container'>\n")

        writer.write("  <INPUT type='hidden' name='${element.name}'")
        if (!value.isNullOrEmpty()) {
            writer.write(" value='$value'")
        }
        if (element.required && !readOnly) {
            writer.write(" required")
        }
        writer.write("/>\n")

        if (!readOnly) {
            writer.write("  <BUTTON type='button' class='btn-upload' rel='${element.name}'>Upload File</BUTTON>\n")
            writer.write("  <INPUT type='file' id='${element.name}-file' name='${element.name}-file' rel='${element.name}' data-upload-url='${context.uploadUrl}'/>\n")
        }

        writer.write("  <SPAN data-name='${element.name}-filename'>\n")
        if (!value.isNullOrEmpty()) {
            val file = context.fileResolver.resolve(value, context.tenantId)
            if (file != null) {
                val filename = StringEscapeUtils.escapeHtml4((file.name))
                val ext = FilenameUtils.getExtension(filename).lowercase()

                writer.write("    <A class='filename' href='${context.downloadUrl}/$value/$filename'><SPAN class='fiv-viv fiv-icon-$ext'></SPAN>&nbsp;$filename</A>\n")
                if (!readOnly) {
                    writer.write("    <button class='btn-close' type='button' name='${element.name}-close' rel='${element.name}'></button>\n")
                }
            }
        }
        writer.write("  </SPAN>\n")

        writer.write("</DIV>\n")
    }
}
