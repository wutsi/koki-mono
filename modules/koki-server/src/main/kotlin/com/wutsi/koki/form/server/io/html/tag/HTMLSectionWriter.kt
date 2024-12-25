package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.server.generator.html.Context
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

class HTMLSectionWriter() : AbstractHTMLElementWriter() {
    override fun doWrite(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        writer.write("<DIV class='section")
        if (readOnly) {
            writer.write(" read-only-section'")
        }
        writer.write(">\n")

        if (!element.title.isNullOrEmpty() || !element.description.isNullOrEmpty()) {
            writer.write("  <DIV class='section-header'>\n")
            if (!element.title.isNullOrEmpty()) {
                writer.write(
                    "    <H2 class='section-title'>" + StringEscapeUtils.escapeHtml4(element.title) + "</H2>\n"
                )
            }
            if (!element.description.isNullOrEmpty()) {
                writer.write(
                    "    <DIV class='section-description'>" + StringEscapeUtils.escapeHtml4(element.description) + "</DIV>\n"
                )
            }
            writer.write("  </DIV>\n")
        }

        writer.write("  <DIV class='section-body'>\n")
        element.elements?.forEach { child ->
            writer.write("    <DIV class='section-item'>\n")
            context.provider
                .get(child.type)
                .write(
                    element = inheritPermissions(element, child),
                    context = context,
                    writer = writer
                )
            writer.write("    </DIV>\n")
        }
        writer.write("  </DIV>\n")
        writer.write("</DIV>\n")
    }

    private fun inheritPermissions(parent: FormElement, child: FormElement): FormElement {
        val accessControl = if (child.accessControl == null && parent.accessControl != null) {
            parent.accessControl
        } else {
            null
        }

        val logic = if (child.logic == null && parent.logic != null) {
            parent.logic
        } else {
            null
        }

        return if (logic != null || accessControl != null) {
            child.copy(
                accessControl = if (accessControl != null) accessControl else child.accessControl,
                logic = if (logic != null) logic else child.logic,
            )
        } else {
            child
        }
    }
}
