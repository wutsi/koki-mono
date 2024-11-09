package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.Form
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

class HTMLFormGenerator {
    fun generate(form: Form, context: Context, writer: StringWriter) {
        writer.write("<FORM method='post' action='${context.submitUrl}'>\n")

        if (!context.successUrl.isNullOrEmpty()) {
            writer.write("  <INPUT type='hidden' name='__success_url' value'${context.submitUrl}'/>\n")
        }
        if (!context.errorUrl.isNullOrEmpty()) {
            writer.write("  <INPUT type='hidden' name='__error_url' value'${context.errorUrl}'/>\n")
        }

        writer.write("  <H1 class='form-title'>${StringEscapeUtils.escapeHtml4(form.content.title)}</H1>\n")
        if (form.content.description != null) {
            writer.write("  <DIV class='form-description'>${StringEscapeUtils.escapeHtml4(form.content.description)}</DIV>\n")
        }

        form.content.elements.forEach { section ->
            context.provider
                .get(section.type)
                .write(section, context, writer)
        }

        writer.write("  <DIV class='form-button-group'>\n")
        writer.write("   <BUTTON type='submit>Submit</BUTTON>\n")
        writer.write("  </DIV>\n")
        writer.write("</FORM>")
    }
}
