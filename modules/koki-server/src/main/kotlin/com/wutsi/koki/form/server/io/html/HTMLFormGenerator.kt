package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormContent
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Service
import java.io.StringWriter

@Service
class HTMLFormGenerator {
    fun generate(content: FormContent, context: Context, writer: StringWriter) {
        writer.write("<DIV class='form ${content.name.lowercase()}'>\n")

        if (!context.readOnly) {
            writer.write("  <FORM method='post' action='${context.submitUrl}'>\n")
        }

        writer.write("    <DIV class='form-header'>\n")
        writer.write("      <H1 class='form-title'>${StringEscapeUtils.escapeHtml4(content.title)}</H1>\n")
        if (content.description != null) {
            writer.write("      <DIV class='form-description'>${StringEscapeUtils.escapeHtml4(content.description)}</DIV>\n")
        }
        writer.write("    </DIV>\n")

        writer.write("    <DIV class='form-body'>\n")
        content.elements.forEach { section ->
            context.provider
                .get(section.type)
                .write(section, context, writer)
        }
        writer.write("    </DIV>\n")

        if (!context.readOnly) {
            writer.write("    <DIV class='form-footer'>\n")
            writer.write("      <DIV class='form-button-group'>\n")
            writer.write("        <BUTTON type='submit'>Submit</BUTTON>\n")
            writer.write("      </DIV>\n")
            writer.write("    </DIV>\n")
            writer.write("  </FORM>\n")
        }

        writer.write("</DIV>")
    }
}
