package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

class HTMLDropdownWriter : AbstractHTMLImputElementWriter() {
    override fun doWriteInput(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        val value = context.data[element.name]

        writer.write("<SELECT name='${element.name}'")
        if (!value.isNullOrEmpty()) {
            writer.write(" value='${StringEscapeUtils.escapeHtml4(value)}'")
        }
        addValidationAttribute(element, writer, readOnly)
        writer.write(">\n")

        element.options
            .forEach { option ->
                val optValue = StringEscapeUtils.escapeHtml4(option.value)
                val optText = StringEscapeUtils.escapeHtml4(option.text ?: optValue)
                writer.write("  <OPTION value='$optValue'>$optText</OPTION>\n")
            }

        writer.write("</SELECT>\n")
    }
}
