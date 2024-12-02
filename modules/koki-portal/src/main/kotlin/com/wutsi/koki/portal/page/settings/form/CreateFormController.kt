package com.wutsi.koki.portal.page.settings.form

import com.wutsi.koki.portal.model.FormModel
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.FormService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class CreateFormController(private val service: FormService) : AbstractPageController() {
    @GetMapping("/settings/forms/create")
    fun show(model: Model): String {
        return create(FormForm(), model)
    }

    private fun create(form: FormForm, model: Model): String {
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_FORM_CREATE,
                title = "New Form"
            ),
        )
        return "settings/forms/create"
    }

    @PostMapping("/settings/forms/add-new")
    fun save(
        @ModelAttribute form: FormForm,
        model: Model
    ): String {
        try {
            val formId = service.create(form)
            val data = FormModel(id = formId, name = form.name, title = form.title)

            model.addAttribute("data", data)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_FORM_SAVED,
                    title = data.longTitle,
                ),
            )
            return "settings/forms/saved"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(form, model)
        }
    }
}
