package com.wutsi.koki.workflow.server.validation

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.rule.ActivityMustNotBeOrphanRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityMustNotHaveSelfAsPredecessorRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityNameMustHavelLessThan100CharactersRule
import com.wutsi.koki.workflow.server.validation.rule.FlowMustHaveValidFromRule
import com.wutsi.koki.workflow.server.validation.rule.FlowMustHaveValidToRule
import com.wutsi.koki.workflow.server.validation.rule.WorkflowMustHaveAtLeastOneStopActivityRule
import com.wutsi.koki.workflow.server.validation.rule.WorkflowMustHaveOneStartActivityRule
import com.wutsi.koki.workflow.server.validation.rule.WorkflowMustNotHaveCycleRule
import org.springframework.stereotype.Service

@Service
class WorkflowValidator(
    private val rules: List<ValidationRule> = listOf(
        ActivityMustNotBeOrphanRule(),
        ActivityMustNotHaveSelfAsPredecessorRule(),
        ActivityNameMustHavelLessThan100CharactersRule(),

        FlowMustHaveValidToRule(),
        FlowMustHaveValidFromRule(),

        WorkflowMustHaveAtLeastOneStopActivityRule(),
        WorkflowMustHaveOneStartActivityRule(),
        WorkflowMustNotHaveCycleRule(), // MUST BE THE LAST
    )
) {
    fun validate(workflow: WorkflowData): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        rules.forEach { rule -> errors.addAll(rule.validate(workflow)) }
        return errors
    }
}
