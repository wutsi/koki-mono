package com.wutsi.koki.portal.page.settings.form

import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.portal.model.FormModel
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.FormService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class EditFormController(
    private val service: FormService,
) : AbstractPageController() {
    @GetMapping("/settings/forms/{id}/edit")
    fun edit(
        @PathVariable id: String,
        model: Model
    ): String {
        val data = service.form(id)
        val content = objectMapper.readValue(data.content, FormContent::class.java)
        val form = FormForm(
            name = data.name,
            title = data.title,
            elements = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(content.elements),
            active = data.active
        )
        return edit(form, data, model)
    }

    private fun edit(form: FormForm, data: FormModel, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute("data", data)

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_FORM_EDIT,
                title = data.longTitle
            ),
        )
        return "settings/forms/edit"
    }

    @PostMapping("/settings/forms/{id}/update")
    fun save(
        @PathVariable id: String,
        @ModelAttribute form: FormForm,
        model: Model
    ): String {
        val data = FormModel(id = id, name = form.name, title = form.title)
        try {
            service.update(id, form)

            model.addAttribute("data", data)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_FORM_SAVED,
                    title = form.name,
                ),
            )
            return "settings/forms/saved"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, data, model)
        }
    }
}
