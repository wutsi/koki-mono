package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

class HTMLParagraphWriter : HTMLTextWriter() {
    override fun doWriteInput(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        val value = context.data[element.name]

        writer.write("<TEXTAREA name='${element.name}'")
        addValidationAttribute(element, writer, readOnly)
        writer.write(">")

        if (!value.isNullOrEmpty()) {
            writer.write(StringEscapeUtils.escapeHtml4(value))
        }

        writer.write("</TEXTAREA>\n")
    }
}
