package com.wutsi.koki.portal.page.settings.form

import com.wutsi.koki.portal.model.FormModel
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.FormService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.client.HttpClientErrorException

@Controller
class ShowFormController(private val service: FormService) : AbstractPageController() {
    @GetMapping("/settings/forms/{id}")
    fun show(
        @PathVariable id: String,
        model: Model,
    ): String {
        val form = service.form(id)
        return show(form, model)
    }

    private fun show(form: FormModel, model: Model): String {
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_FORM,
                title = form.name
            ),
        )
        return "settings/forms/show"
    }

    @GetMapping("/settings/forms/{id}/delete")
    fun delete(
        @PathVariable id: String,
        model: Model,
    ): String {
        val form = service.form(id)
        try {
            service.delete(id)

            model.addAttribute("form", form)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_FORM_DELETED,
                    title = form.longTitle
                ),
            )
            return "settings/forms/deleted"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(form, model)
        }
    }
}
