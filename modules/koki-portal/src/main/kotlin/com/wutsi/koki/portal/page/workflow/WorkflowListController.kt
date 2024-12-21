package com.wutsi.koki.portal.page.workflow

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.WorkflowInstanceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class WorkflowListController(
    private val workflowInstanceService: WorkflowInstanceService,
) : AbstractPageController() {
    @GetMapping("/workflows")
    fun list(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        more(limit, offset, model)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_LIST,
                title = "Workflow Instances"
            )
        )
        return "workflows/list"
    }

    @GetMapping("/workflows/more")
    fun more(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        val workflowInstances = workflowInstanceService.workflows(limit = limit, offset = offset)
        if (workflowInstances.isNotEmpty()) {
            model.addAttribute("workflowInstances", workflowInstances)

            if (workflowInstances.size >= limit) {
                val nextOffset = offset + limit
                val url = "/workflows/more?limit=$limit&offset=$nextOffset"
                model.addAttribute("moreUrl", url)
            }
        }
        return "workflows/list-more"
    }
}
