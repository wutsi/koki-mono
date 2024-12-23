package com.wutsi.koki.workflow.server.validation

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.service.WorkflowExpressionEvaluator
import com.wutsi.koki.workflow.server.validation.rule.ActivitiesShouldNotHaveMoreThanOneFlowRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityEndMustNotHaveSuccessorRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityManualMustHaveARoleRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityMustHaveANameRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityMustNotBeOrphanRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityMustNotHaveSelfAsPredecessorRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityNameMustHavelLessThan100CharactersRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityReceiveMustHaveAnEventRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityScriptMustHaveAScriptRule
import com.wutsi.koki.workflow.server.validation.rule.ActivitySendMustHaveAMessageRule
import com.wutsi.koki.workflow.server.validation.rule.ActivitySendMustHaveARoleOrRecipientRule
import com.wutsi.koki.workflow.server.validation.rule.ActivitySendRecipientMustHaveAnEmailRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityServiceMustHaveAServiceRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityServiceMustHaveAValidMethodRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityStartMustNotHavePredecessorRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityUserMustHaveAFormRule
import com.wutsi.koki.workflow.server.validation.rule.ActivityUserMustHaveARoleRule
import com.wutsi.koki.workflow.server.validation.rule.FlowExpressionMustBeValidRule
import com.wutsi.koki.workflow.server.validation.rule.FlowMustHaveValidFromRule
import com.wutsi.koki.workflow.server.validation.rule.FlowMustHaveValidToRule
import com.wutsi.koki.workflow.server.validation.rule.WorkflowMustHaveANameRule
import com.wutsi.koki.workflow.server.validation.rule.WorkflowMustHaveAtLeastOneEndActivityRule
import com.wutsi.koki.workflow.server.validation.rule.WorkflowMustHaveOneStartActivityRule
import com.wutsi.koki.workflow.server.validation.rule.WorkflowMustNotHaveCycleRule
import com.wutsi.koki.workflow.server.validation.rule.WorkflowWithApprovalMustHaveApproverRoleRule
import org.springframework.stereotype.Service

@Service
class WorkflowValidator(
    private val expressionEvaluator: WorkflowExpressionEvaluator,
    private val rules: List<ValidationRule> = listOf(
        ActivityMustHaveANameRule(),
        ActivitiesShouldNotHaveMoreThanOneFlowRule(),
        ActivityMustNotBeOrphanRule(),
        ActivityMustNotHaveSelfAsPredecessorRule(),
        ActivityNameMustHavelLessThan100CharactersRule(),
        ActivitySendRecipientMustHaveAnEmailRule(),
        ActivityEndMustNotHaveSuccessorRule(),
        ActivityManualMustHaveARoleRule(),
        ActivityReceiveMustHaveAnEventRule(),
        ActivityServiceMustHaveAServiceRule(),
        ActivityServiceMustHaveAValidMethodRule(),
        ActivityScriptMustHaveAScriptRule(),
        ActivitySendMustHaveAMessageRule(),
        ActivitySendMustHaveARoleOrRecipientRule(),
        ActivityStartMustNotHavePredecessorRule(),
        ActivityUserMustHaveARoleRule(),
        ActivityUserMustHaveAFormRule(),

        FlowMustHaveValidToRule(),
        FlowMustHaveValidFromRule(),
        FlowExpressionMustBeValidRule(expressionEvaluator),

        WorkflowMustHaveANameRule(),
        WorkflowWithApprovalMustHaveApproverRoleRule(),
        WorkflowMustHaveAtLeastOneEndActivityRule(),
        WorkflowMustHaveOneStartActivityRule(),
        WorkflowMustNotHaveCycleRule(), // MUST BE THE LAST
    )
) {
    fun ruleCount(): Int {
        return rules.size
    }

    fun validate(workflow: WorkflowData): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        rules.forEach { rule -> errors.addAll(rule.validate(workflow)) }
        return errors
    }
}
