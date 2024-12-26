package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.dto.FormAction
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

    protected fun canView(element: FormElement, context: Context, action: FormAction): Boolean {
        if (context.preview) {
            return true
        } else if (action == FormAction.HIDE) {
            return false
        } else if (element.accessControl == null || element.accessControl?.viewerRoles == null) {
            return true
        } else {
            return checkPermission(element.accessControl!!.viewerRoles!!, context)
        }
    }

    protected fun canEdit(element: FormElement, context: Context, action: FormAction): Boolean {
        if (context.preview || action == FormAction.DISABLE || element.readOnly == true || context.readOnly) {
            return false
        } else if (element.accessControl == null || element.accessControl?.editorRoles == null) {
            return true
        } else {
            return checkPermission(element.accessControl!!.editorRoles!!, context)
        }
    }

    private fun checkPermission(roles: List<String>, context: Context): Boolean {
        return roles.find { role -> context.roleNames.contains(role) } != null
    }

    override fun write(element: FormElement, context: Context, writer: StringWriter) {
        val action = element.logic?.let { logic ->
            context.formLogicEvaluator.evaluate(logic, context.data)
        } ?: FormAction.NONE
        if (canView(element, context, action)) {
            val readOnly = !canEdit(element, context, action)
            doWrite(element, context, writer, readOnly)
        }
    }
}
