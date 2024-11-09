package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import java.io.StringWriter

abstract class HTMLBaseElementWriter() : HTMLElementWriter {
    protected abstract fun doWrite(
        element: FormElement,
        context: Context,
        writer: StringWriter,
        readOnly: Boolean,
    )

    protected fun canView(element: FormElement, context: Context): Boolean {
        if (element.accessControl == null) {
            return true
        }
        return element.accessControl!!.viewerRoles.contains(context.roleName)
    }

    protected fun canEdit(element: FormElement, context: Context): Boolean {
        if (element.accessControl == null) {
            return true
        }
        return element.accessControl!!.editorRoles.contains(context.roleName)
    }

    override fun write(element: FormElement, context: Context, writer: StringWriter) {
        if (canView(element, context)) {
            val readOnly = !canEdit(element, context)
            doWrite(element, context, writer, readOnly)
        }
    }
}
