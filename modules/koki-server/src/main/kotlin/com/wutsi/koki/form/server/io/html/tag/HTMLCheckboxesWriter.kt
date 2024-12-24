package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.server.generator.html.Context
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

class HTMLCheckboxesWriter : AbstractHTMLImputElementWriter() {
    override fun doWriteInput(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        val value = context.data[element.name]
        val values = if (value == null) {
            emptyList<String>()
        } else if (value is Collection<*>) {
            value
        } else {
            listOf(value.toString())
        }

        val type = getType(element)
        writer.write("<DIV class='$type-container'")
        if (element.required == true) {
            writer.append(" required")
        }
        writer.write(">\n")

        element.options.forEach { option ->
            input(
                name = element.name,
                value = option.value,
                text = (option.text ?: option.value),
                type = getType(element),
                readOnly = readOnly,
                checked = values.contains(option.value),
                writer = writer
            )
        }

        if (element.otherOption != null) {
            input(
                name = element.name,
                value = element.otherOption!!.value,
                text = (element.otherOption!!.text ?: element.otherOption!!.value),
                type = "text",
                readOnly = readOnly,
                checked = values.contains(element.otherOption!!.value),
                writer = writer
            )
        }

        writer.write("</DIV>\n")
    }

    private fun input(
        name: String,
        value: String,
        text: String,
        type: String,
        readOnly: Boolean,
        checked: Boolean,
        writer: StringWriter
    ) {
        writer.write("  <DIV class='item'>\n")

        // Input
        writer.write("    <INPUT name='$name' type='$type' value='${StringEscapeUtils.escapeHtml4(value)}'")
        if (readOnly) {
            writer.write(" disabled")
        }
        if (checked) {
            writer.write(" checked")
        }
        writer.write("/>\n")

        // Text
        writer.write("    <LABEL>${StringEscapeUtils.escapeHtml4(text)}</LABEL>\n")

        writer.write("  </DIV>\n")
    }

    private fun getType(element: FormElement): String {
        return if (element.type == FormElementType.CHECKBOXES) {
            "radio"
        } else if (element.type == FormElementType.MULTIPLE_CHOICE) {
            "checkbox"
        } else {
            ""
        }
    }
}
