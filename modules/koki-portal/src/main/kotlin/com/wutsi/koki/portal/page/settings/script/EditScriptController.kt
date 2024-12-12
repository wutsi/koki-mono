package com.wutsi.koki.portal.page.settings.script

import com.wutsi.koki.portal.model.PageModel
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
class EditScriptController(private val service: ScriptService) : AbstractPageController() {
    @GetMapping("/settings/scripts/{id}/edit")
    fun edit(@PathVariable id: String, model: Model): String {
        val script = service.script(id)
        return edit(script, model)
    }

    private fun edit(script: ScriptModel, model: Model): String {
        model.addAttribute("script", script)
        model.addAttribute(
            "form",
            ScriptForm(
                name = script.name,
                title = script.title,
                language = script.language.name.lowercase(),
                code = script.code,
                parameters = script.parameters.joinToString(separator = "\n"),
                active = script.active,
                description = script.description ?: "",
            )
        )
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_SCRIPT_EDIT,
                title = script.longTitle
            )
        )
        return "settings/scripts/edit"
    }

    @PostMapping("/settings/scripts/{id}/update")
    fun update(
        @PathVariable id: String,
        @ModelAttribute form: ScriptForm,
        model: Model
    ): String {
        val script = service.script(id)
        try {
            service.update(id, form)

            model.addAttribute("script", ScriptModel(id = id, name = form.name, title = form.title))
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_SCRIPT_SAVED,
                    title = script.longTitle,
                ),
            )
            return "settings/scripts/saved"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(script, model)
        }
    }
}
