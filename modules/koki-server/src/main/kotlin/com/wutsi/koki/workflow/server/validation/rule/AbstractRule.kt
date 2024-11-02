package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.server.validation.ValidationError
import com.wutsi.koki.workflow.server.validation.ValidationRule

abstract class AbstractRule : ValidationRule {
    protected fun createError(type: String, name: String, values: List<String> = emptyList()): ValidationError {
        val message = if (values.isEmpty()) {
            this::class.java.simpleName
        } else {
            "${this::class.java.simpleName} - $values"
        }
        return ValidationError("$type: $name", message)
    }
}
