package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

abstract class AbstractHTMLImputElementWriter() : AbstractHTMLElementWriter() {
    protected abstract fun doWriteInput(
        element: FormElement,
        context: Context,
        writer: StringWriter,
        readOnly: Boolean,
    )

    override fun doWrite(
        element: FormElement,
        context: Context,
        writer: StringWriter,
        readOnly: Boolean,
    ) {
        if (!element.title.isNullOrEmpty()) {
            writer.write("<LABEL class='title'><SPAN>" + StringEscapeUtils.escapeHtml4(element.title) + "</SPAN>")
            if (element.required) {
                writer.write("<SPAN class='required'>*</SPAN>")
            }
            writer.write("</LABEL>\n")
        }
        if (!element.description.isNullOrEmpty()) {
            writer.write("<DIV class='description'>" + StringEscapeUtils.escapeHtml4(element.description) + "</DIV>\n")
        }

        doWriteInput(element, context, writer, readOnly)
    }

    protected fun addValidationAttribute(element: FormElement, writer: StringWriter, readOnly: Boolean) {
        // Read only
        if (readOnly) {
            writer.write(" readonly")
        }

        // Required
        if (element.required) {
            writer.write(" required")
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
}
