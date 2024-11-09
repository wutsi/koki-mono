package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

class HTMLTextWriter : AbstractHTMLImputElementWriter() {
    override fun doWriteInput(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        val value = context.data[element.name]

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

        writer.write("/>")
    }

    private fun addValidationAttribute(element: FormElement, writer: StringWriter, readOnly: Boolean) {
        // Read only
        if (readOnly) {
            writer.write(" readonly='readonly'")
        }

        // Required
        if (element.required) {
            writer.write(" required='required'")
        }

        // Min
        if (element.min != null) {
            writer.write(" min='${StringEscapeUtils.escapeHtml4(element.min)}'")
        }

        // Max
        if (element.max != null) {
            writer.write(" max='${StringEscapeUtils.escapeHtml4(element.max)}'")
        }

        // Min
        if (element.minLength != null) {
            writer.write(" minlength='${element.minLength}'")
        }

        // Maxlength
        if (element.maxLength != null) {
            writer.write(" maxlength='${element.maxLength}'")
        }

        // Pattern
        if (element.pattern != null) {
            writer.write(" pattern='${StringEscapeUtils.escapeHtml4(element.pattern)}'")
        }
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
