package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.HTMLElementWriter
import java.io.StringWriter

abstract class AbstractHTMLElementWriter() : HTMLElementWriter {
    protected abstract fun doWrite(
        element: FormElement,
        context: Context,
        writer: StringWriter,
        readOnly: Boolean,
    )

    protected fun canView(element: FormElement, context: Context): Boolean {
        if (element.accessControl == null || element.accessControl?.viewerRoles == null) {
            return true
        }
        element.accessControl!!.viewerRoles?.forEach { role ->
            if (context.roleNames.contains(role)) {
                return true
            }
        }
        return false
    }

    protected fun canEdit(element: FormElement, context: Context): Boolean {
        if (element.readOnly == true || context.readOnly) {
            return false
        }
        if (element.accessControl == null || element.accessControl?.editorRoles == null) {
            return true
        }
        element.accessControl!!.editorRoles?.forEach { role ->
            if (context.roleNames.contains(role)) {
                return true
            }
        }
        return false
    }

    override fun write(element: FormElement, context: Context, writer: StringWriter) {
        if (canView(element, context)) {
            val readOnly = !canEdit(element, context)
            doWrite(element, context, writer, readOnly)
        }
    }
}
