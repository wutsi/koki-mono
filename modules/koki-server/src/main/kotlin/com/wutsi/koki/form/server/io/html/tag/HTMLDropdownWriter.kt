package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.server.generator.html.Context
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

class HTMLDropdownWriter : AbstractHTMLImputElementWriter() {
    override fun doWriteInput(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        val value = context.data[element.name]?.toString()

        writer.write("<SELECT name='${element.name}'")
        if (!value.isNullOrEmpty()) {
            writer.write(" value='${StringEscapeUtils.escapeHtml4(value)}'")
        }
        addValidationAttribute(element, writer, readOnly)
        writer.write(">\n")

        element.options
            .forEach { option ->
                val optText = option.text ?: option.value

                writer.write("  <OPTION value='${StringEscapeUtils.escapeHtml4(option.value)}'")
                if (value == option.value) {
                    writer.write(" selected")
                } else if (readOnly) {
                    writer.write(" disabled")
                }
                writer.write(">${StringEscapeUtils.escapeHtml4(optText)}</OPTION>\n")
            }

        writer.write("</SELECT>\n")
    }
}
