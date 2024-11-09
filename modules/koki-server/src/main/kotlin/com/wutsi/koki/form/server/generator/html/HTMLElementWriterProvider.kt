package com.wutsi.koki.form.server.generator.html

import java.io.StringWriter


interface HTMLElementWriter<T> {
    fun generate(element: T, writer: StringWriter)
}
