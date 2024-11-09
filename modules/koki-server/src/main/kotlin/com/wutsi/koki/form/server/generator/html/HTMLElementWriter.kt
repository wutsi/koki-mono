package com.wutsi.koki.form.server.generator.html

interface HTMLElementGenerator<T> {
    fun generate(element: T)
}
