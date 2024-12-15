package com.wutsi.koki.portal.page.settings.script

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.model.ScriptExecutionModel
import com.wutsi.koki.portal.model.ScriptModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.script.ScriptForm
import com.wutsi.koki.portal.service.ScriptService
import com.wutsi.koki.script.dto.Language
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException

@Controller
class RunScriptController(private val service: ScriptService) : AbstractPageController() {
    @GetMapping("/settings/scripts/{id}/run")
    fun run(@PathVariable id: String, model: Model): String {
        val script = service.script(id)
        val form = ScriptForm(
            name = script.name,
            title = script.title,
            language = script.language.name.lowercase(),
            code = script.code,
            parameters = script.parameters
                .map { param -> "$param=1" }
                .joinToString(separator = "\n"),
        )
        return run(script, form, null, model)
    }

    private fun run(
        script: ScriptModel,
        form: ScriptForm,
        execution: ScriptExecutionModel?,
        model: Model
    ): String {
        model.addAttribute("script", script)
        execution?.let { model.addAttribute("execution", execution) }
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_SCRIPT_RUN,
                title = script.longTitle
            )
        )
        return "settings/scripts/run"
    }

    @PostMapping("/settings/scripts/{id}/run")
    fun run(
        @PathVariable id: String,
        @ModelAttribute form: ScriptForm,
        model: Model
    ): String {
        val script = service.script(id).copy(
            language = Language.valueOf(form.language.uppercase()),
            code = form.code,
        )
        try {
            val execution = service.run(form)
            return run(script, form, execution, model)
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            val execution = ScriptExecutionModel(
                errorCode = errorResponse.error.code,
                errorMessage = errorResponse.error.message,
                console = errorResponse.error.data?.get("console")?.toString()
            )
            return run(script, form, execution, model)
        }
    }

    @ResponseBody
    @PostMapping("/settings/scripts/{id}/run/update")
    fun update(
        @PathVariable id: String,
        @ModelAttribute form: ScriptForm
    ): Map<String, Any> {
        val script = service.script(id)
        service.update(
            id,
            ScriptForm(
                name = script.name,
                title = script.title,
                description = (script.description ?: ""),
                language = form.language,
                code = form.code,
                active = script.active,
                parameters = form.parameters
                    .split("\n").toList()
                    .map { param -> param.trim() }
                    .filter { param -> param.isNotEmpty() }
                    .map { param ->
                        val parts = param.split("=")
                        if (parts.size > 0) {
                            parts[0].trim()
                        } else {
                            param
                        }
                    }
                    .joinToString(separator = "\n"),
            )
        )
        return mapOf("id" to script.id)
    }
}
