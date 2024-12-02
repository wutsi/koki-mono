package com.wutsi.koki.portal.page.workflow.instance

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.WorkflowInstanceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class WorkflowController(
    private val service: WorkflowInstanceService
) : AbstractPageController() {
    @GetMapping("/workflows/{id}")
    fun show(
        @PathVariable id: String,
        model: Model
    ): String {
        val workflowInstance = service.workflow(id)
        model.addAttribute("workflowInstance", workflowInstance)
        model.addAttribute(
            "activityInstances",
            workflowInstance.activityInstances.map { it.activity.id to it }.toMap()
        )

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW,
                title = workflowInstance.workflow.longTitle
            )
        )
        return "workflows/show"
    }
}
