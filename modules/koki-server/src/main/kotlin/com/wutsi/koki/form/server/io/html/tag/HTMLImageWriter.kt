package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.server.generator.html.Context
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

class HTMLImageWriter : AbstractHTMLElementWriter() {
    override fun doWrite(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        writer.write("<IMG src='${element.url}'")
        if (!element.title.isNullOrEmpty()) {
            writer.write(" alt='" + StringEscapeUtils.escapeHtml4(element.title) + "'")
        }
        writer.write("/>\n")
    }
}
