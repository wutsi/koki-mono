package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

class HTMLCheckboxesWriter : AbstractHTMLImputElementWriter() {
    override fun doWriteInput(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        val value = context.data[element.name]

        element.options.forEach { option ->
            input(
                name = element.name,
                value = option.value,
                text = (option.text ?: option.value),
                type = getType(element),
                readOnly = readOnly,
                checked = (option.value == value),
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
                checked = (element.otherOption!!.value == value),
                writer = writer
            )
        }
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
        writer.write("<DIV class='item'>\n")

        // Input
        writer.write("  <INPUT name='$name' type='$type' value='${StringEscapeUtils.escapeHtml4(value)}'")
        if (readOnly) {
            writer.write(" readonly")
        }
        if (checked) {
            writer.write(" checked")
        }
        writer.write("/>\n")

        // Text
        writer.write("  <LABEL>${StringEscapeUtils.escapeHtml4(text)}</LABEL>\n")

        writer.write("</DIV>\n")
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
