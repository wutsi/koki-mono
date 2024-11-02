package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivityNameMustHavelLessThan100CharactersRule : AbstractActivityRule() {
    companion object {
        private const val MAX_LENGTH = 100
    }

    override fun validate(workflow: WorkflowData): List<ValidationError> {
        return workflow.activities
            .filter { activity -> activity.name.length > MAX_LENGTH }
            .map { activity -> createError(activity) }
    }
}
