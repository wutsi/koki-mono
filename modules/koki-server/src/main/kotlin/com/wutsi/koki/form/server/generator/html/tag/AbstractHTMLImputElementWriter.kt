package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import java.io.StringWriter

abstract class HTMLImputElementWriter() : HTMLBaseElementWriter {
    protected abstract fun doWriteInput(
        element: FormElement,
        context: Context,
        writer: StringWriter,
        readOnly: Boolean,
    )
    
    protected fun doWrite(
        element: FormElement,
        context: Context,
        writer: StringWriter,
        readOnly: Boolean,
    ) {

    }

}
