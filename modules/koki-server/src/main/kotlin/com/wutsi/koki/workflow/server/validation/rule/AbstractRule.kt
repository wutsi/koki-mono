package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.server.validation.ValidationError
import com.wutsi.koki.workflow.server.validation.ValidationRule

abstract class AbstractActivityRule : ValidationRule {
    protected fun createError(activity: ActivityData, values: List<String> = emptyList()): ValidationError {
        val message = if (values.isEmpty()) {
            this::class.java.simpleName
        } else {
            "${this::class.java.simpleName} - $values"
        }
        return ValidationError("activity: ${activity.name}", message)
    }
}
