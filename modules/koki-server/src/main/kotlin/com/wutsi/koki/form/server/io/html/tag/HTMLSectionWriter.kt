package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

class HTMLSectionWriter() : AbstractHTMLElementWriter() {
    override fun doWrite(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        writer.write("<DIV class='section'>\n")

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
            writer.write("      ")
            context.provider
                .get(child.type)
                .write(
                    element = inheritAccessControl(element, child),
                    context = context,
                    writer = writer
                )
            writer.write("    </DIV>\n")
        }
        writer.write("  </DIV>\n")
        writer.write("</DIV>\n")
    }

    private fun inheritAccessControl(parent: FormElement, child: FormElement): FormElement {
        return if (child.accessControl == null && parent.accessControl != null) {
            child.copy(
                accessControl = parent.accessControl!!.copy()
            )
        } else {
            child
        }
    }
}
