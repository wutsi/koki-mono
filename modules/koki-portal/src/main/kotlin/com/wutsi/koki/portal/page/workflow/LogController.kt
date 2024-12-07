package com.wutsi.koki.portal.page.workflow.instance

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.LogService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class LogController(private val service: LogService) : AbstractPageController() {
    @GetMapping("/workflows/logs/{id}")
    fun show(
        @PathVariable id: String,
        model: Model
    ): String {
        val log = service.log(id)

        model.addAttribute("log", log)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.LOG,
                title = "Log",
            )
        )
        return "workflows/log"
    }
}
