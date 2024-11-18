package com.wutsi.koki.portal.page.workflow

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.rest.WorkflowService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class StartWorkflowInstanceController(
    private val workflowService: WorkflowService,
) : AbstractPageController() {
    @GetMapping("/workflows/{id}/start")
    fun new(
        @PathVariable id: Long,
        model: Model
    ): String {
        val workflow = workflowService.workflow(id)
        model.addAttribute("workflow", workflow)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_START,
                title = workflow.longTitle,
            )
        )
        return "workflows/start"
    }
}
