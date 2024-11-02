package com.wutsi.koki.workflow.server.validation

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.rule.ActivityMustHaveValidPredecessorRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityMustNotBeOrphanRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityMustNotHaveSelfAsPredecessorRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityNameMustHavelLessThan100CharactersRule
import com.wutsi.koki.workflow.server.validation.rule.WorkflowMustHaveAtLeastOneStopActivityRule
import com.wutsi.koki.workflow.server.validation.rule.WorkflowMustHaveOneStartActivityRule
import org.springframework.stereotype.Service

@Service
class WorkflowValidator(
    private val rules: List<ValidationRule> = listOf(
        ActivityMustHaveValidPredecessorRule(),
        ActivityMustNotBeOrphanRule(),
        ActivityMustNotHaveSelfAsPredecessorRule(),
        ActivityNameMustHavelLessThan100CharactersRule(),

        WorkflowMustHaveAtLeastOneStopActivityRule(),
        WorkflowMustHaveOneStartActivityRule(),
    )
) {
    fun validate(workflow: WorkflowData): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        rules.forEach { rule -> errors.addAll(rule.validate(workflow)) }
        return errors
    }
}
