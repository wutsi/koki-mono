package com.wutsi.koki.portal.page.workflow.widget

import com.wutsi.koki.portal.service.CurrentUserHolder
import com.wutsi.koki.portal.service.WorkflowInstanceService
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MyCasesWidgetController(
    private val workflowInstanceService: WorkflowInstanceService,
    private val currentUserHolder: CurrentUserHolder,
) {
    @GetMapping("/workflows/widgets/my-cases")
    fun show(model: Model): String {
        val me = currentUserHolder.get()
        if (me != null) {
            val workflowInstances = workflowInstanceService.workflows(
                status = WorkflowStatus.RUNNING,
                createdById = me.id,
            )
            if (workflowInstances.isNotEmpty()) {
                model.addAttribute("workflowInstances", workflowInstances)
            }
        }
        return "workflows/widgets/my-cases"
    }
}
