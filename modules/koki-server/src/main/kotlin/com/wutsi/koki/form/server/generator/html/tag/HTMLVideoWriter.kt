package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter

class HTMLImageWriter : HTMLBaseElementWriter() {
    override fun doWrite(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        if (!canView(element, context)) {
            return
        }

        val buff = StringBuilder("<IMG src='${element.url}'")
        if (!element.title.isNullOrEmpty()) {
            buff.append(" alt='")
                .append(StringEscapeUtils.escapeHtml4(element.title))
                .append("'")
        }
        buff.append("/>\n")
        writer.write(buff.toString())
    }
}
