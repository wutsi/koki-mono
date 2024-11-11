package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

open class HTMLTextWriter : AbstractHTMLImputElementWriter() {
    override fun doWriteInput(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        val value = context.data[element.name]?.toString()

        writer.write("<INPUT name='${element.name}'")

        // Type
        val type = getInputType(element)
        if (type != null) {
            writer.write(" type='$type'")
        }

        // Value
        if (!value.isNullOrEmpty()) {
            writer.write(" value='${StringEscapeUtils.escapeHtml4(value)}'")
        }

        // Validation
        addValidationAttribute(element, writer, readOnly)

        writer.write("/>\n")
    }

    private fun getInputType(element: FormElement): String? {
        return if (element.type == FormElementType.NUMBER) {
            "number"
        } else if (element.type == FormElementType.EMAIL) {
            "email"
        } else if (element.type == FormElementType.URL) {
            "url"
        } else if (element.type == FormElementType.DATE) {
            "date"
        } else if (element.type == FormElementType.TIME) {
            "time"
        } else {
            null
        }
    }
}
