package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.server.validation.ValidationError

abstract class AbstractActivityRule : AbstractRule() {
    protected fun createError(activity: ActivityData, values: List<String> = emptyList()): ValidationError =
        createError("activity", activity.name, values)
}
