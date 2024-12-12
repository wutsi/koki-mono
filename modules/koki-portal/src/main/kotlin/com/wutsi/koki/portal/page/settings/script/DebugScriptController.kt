package com.wutsi.koki.portal.page.settings.script

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.model.ScriptExecutionModel
import com.wutsi.koki.portal.model.ScriptModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.script.ScriptForm
import com.wutsi.koki.portal.service.ScriptService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class DebugScriptController(private val service: ScriptService) : AbstractPageController() {
    @GetMapping("/settings/scripts/{id}/debug")
    fun debug(@PathVariable id: String, model: Model): String {
        val script = service.script(id)
        return debug(script, null, model)
    }

    private fun debug(script: ScriptModel, execution: ScriptExecutionModel?, model: Model): String {
        model.addAttribute("script", script)
        execution?.let { model.addAttribute("execution", execution) }
        model.addAttribute(
            "form",
            ScriptForm(
                name = script.name,
                title = script.title,
                language = script.language.name.lowercase(),
                code = script.code,
                parameters = script.parameters
                    .map { param -> "$param=1" }
                    .joinToString(separator = "\n"),
            )
        )
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_SCRIPT_DEBUG,
                title = script.longTitle
            )
        )
        return "settings/scripts/debug"
    }

    @PostMapping("/settings/scripts/{id}/debug")
    fun execute(
        @PathVariable id: String,
        @ModelAttribute form: ScriptForm,
        model: Model
    ): String {
        val script = service.script(id)
        try {
            val execution = service.execute(id, form)
            return debug(script, execution, model)
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            val execution = ScriptExecutionModel(
                errorCode = errorResponse.error.code,
                errorMessage = errorResponse.error.message,
                console = errorResponse.error.data?.get("console")?.toString()
            )
            return debug(script, execution, model)
        }
    }
}
