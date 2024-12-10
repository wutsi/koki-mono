package com.wutsi.koki.portal.page.settings.script

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.ScriptService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import kotlin.collections.isNotEmpty

@Controller
class ListScriptController(private val service: ScriptService) : AbstractPageController() {
    @GetMapping("/settings/scripts")
    fun list(model: Model): String {
        val scripts = service.scripts()
        if (scripts.isNotEmpty()) {
            model.addAttribute("scripts", scripts)
        }
        model.addAttribute(
            "page",
            PageModel(name = PageName.SETTINGS_SCRIPT_LIST, title = "Scripts"),
        )
        return "settings/scripts/list"
    }
}
