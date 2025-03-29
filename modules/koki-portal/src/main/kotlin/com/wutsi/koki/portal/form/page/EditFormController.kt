package com.wutsi.koki.portal.form.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.form.form.FormForm
import com.wutsi.koki.portal.form.model.FormModel
import com.wutsi.koki.portal.form.service.FormService
import com.wutsi.koki.portal.security.RequiresPermission
import jdk.internal.editor.external.ExternalEditor.edit
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/forms")
@RequiresPermission(["form:manage"])
class EditFormController(
    private val service: FormService
) : AbstractFormController() {
    @GetMapping("/{id}/edit")
    fun create(
        @PathVariable id: Long,
        model: Model,
    ): String {
        val entity = service.form(id)
        val form = FormForm(
            name = entity.name,
            description = entity.description,
            active = entity.active
        )
        return edit(form, entity, model)
    }

    @PostMapping("/{id}/update")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: FormForm,
        model: Model
    ): String {
        try {
            service.update(id, form)
            return "redirect:/forms/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            val entity = service.form(id)
            return edit(form, entity, model)
        }
    }

    private fun edit(form: FormForm, entity: FormModel, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute("entity", entity)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FORM_EDIT,
                title = entity.name,
            )
        )
        return "forms/edit"
    }
}
