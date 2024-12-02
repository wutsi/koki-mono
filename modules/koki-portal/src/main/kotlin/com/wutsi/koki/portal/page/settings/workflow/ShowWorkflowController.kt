package com.wutsi.koki.portal.page.workflow

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.WorkflowService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ShowWorkflowController(
    private val service: WorkflowService,
) : AbstractPageController() {
    @GetMapping("/settings/workflows/{id}")
    fun show(
        @PathVariable id: Long,
        model: Model
    ): String {
        val workflow = service.workflow(id)
        model.addAttribute("workflow", workflow)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_WORKFLOW,
                title = workflow.longTitle,
            )
        )
        return "settings/workflows/show"
    }
}
