package com.wutsi.koki.portal.page.settings.workflow

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.WorkflowService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ListWorkflowController(
    private val service: WorkflowService,
) : AbstractPageController() {
    @GetMapping("/settings/workflows")
    fun list(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val workflows = service.workflows(limit = limit, offset = offset)
        if (workflows.isNotEmpty()) {
            model.addAttribute("workflows", workflows)
        }
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_LIST,
                title = "Workflows",
            )
        )

        return "settings/workflows/list"
    }
}
