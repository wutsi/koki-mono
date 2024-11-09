package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.Element
import java.io.StringWriter


interface HTMLElementWriter {
    fun generate(element: Element, writer: StringWriter)
}
