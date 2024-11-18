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
class WorkflowController(
    private val service: WorkflowService,
) : AbstractPageController() {
    @GetMapping("/workflows/{id}")
    fun new(
        @PathVariable id: Long,
        model: Model
    ): String {
        val workflow = service.workflow(id)
        model.addAttribute("workflow", workflow)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW,
                title = "${workflow.name} - ${workflow.title}",
            )
        )
        return "workflows/index"
    }
}
