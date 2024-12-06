package com.wutsi.koki.portal.page.settings.form

import com.fasterxml.jackson.core.JacksonException
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
            json = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(content),
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
        try {
            service.update(id, form)

            val data = toFormModel(id, form)
            model.addAttribute("data", data)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_FORM_SAVED,
                    title = data.name,
                ),
            )
            return "settings/forms/saved"
        } catch (ex: Exception) {
            if (ex is HttpClientErrorException) {
                val errorResponse = toErrorResponse(ex)
                model.addAttribute("error", errorResponse.error.code)
            } else if (ex is JacksonException) {
                model.addAttribute("error", "The JSON is not valid")
            } else {
                throw ex
            }
            return edit(form, FormModel(id = id), model)
        }
    }

    private fun toFormModel(id: String, form: FormForm): FormModel {
        val content = objectMapper.readValue(form.json, FormContent::class.java)
        return FormModel(id = id, name = content.name, title = content.title)
    }
}
