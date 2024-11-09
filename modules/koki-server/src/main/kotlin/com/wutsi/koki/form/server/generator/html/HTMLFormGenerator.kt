package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import java.io.StringWriter

interface HTMLElementWriter {
    fun generate(element: FormElement, writer: StringWriter, context: Context)
}
