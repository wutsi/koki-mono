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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class CreateScriptController(private val service: ScriptService) : AbstractPageController() {
    @GetMapping("/settings/scripts/create")
    fun create(model: Model): String {
        return create(ScriptForm(), model)
    }

    private fun create(form: ScriptForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_SCRIPT_CREATE,
                title = "Script"
            )
        )
        return "settings/scripts/create"
    }

    @PostMapping("/settings/scripts/add-new")
    fun addNew(
        @ModelAttribute form: ScriptForm,
        model: Model
    ): String {
        try {
            val id = service.create(form)

            val script = ScriptModel(id = id, name = form.name, title = form.title)
            model.addAttribute("script", script)
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
            return create(form, model)
        }
    }
}
