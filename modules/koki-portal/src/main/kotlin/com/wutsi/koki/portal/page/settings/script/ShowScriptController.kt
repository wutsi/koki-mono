package com.wutsi.koki.portal.page.settings.script

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.model.ScriptModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.ScriptService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.client.HttpClientErrorException

@Controller
class ShowScriptController(private val service: ScriptService) : AbstractPageController() {
    @GetMapping("/settings/scripts/{id}")
    fun show(@PathVariable id: String, model: Model): String {
        val script = service.script(id)
        return show(script, model)
    }

    private fun show(script: ScriptModel, model: Model): String {
        model.addAttribute("script", script)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_SCRIPT,
                title = script.longTitle
            )
        )
        return "settings/scripts/show"
    }

    @GetMapping("/settings/scripts/{id}/delete")
    fun delete(@PathVariable id: String, model: Model): String {
        val script = service.script(id)
        try {
            service.delete(id)

            model.addAttribute("script", script)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_SCRIPT_DELETED,
                    title = script.longTitle
                ),
            )
            return "settings/scripts/deleted"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(script, model)
        }
    }
}
