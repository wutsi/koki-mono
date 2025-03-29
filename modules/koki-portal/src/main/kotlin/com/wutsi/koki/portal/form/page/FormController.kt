package com.wutsi.koki.portal.form.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.form.form.FormForm
import com.wutsi.koki.portal.form.service.FormService
import com.wutsi.koki.portal.security.RequiresPermission
import jdk.internal.editor.external.ExternalEditor.edit
import jdk.jfr.internal.handlers.EventHandler.timestamp
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/forms")
@RequiresPermission(["form"])
class CreateFormController(
    private val service: FormService
) : AbstractFormController() {
    @GetMapping("/create")
    fun create(
        model: Model,
    ): String {
        val form = FormForm()
        return create(form, model)
    }

    private fun create(form: FormForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FORM_CREATE,
                title = "Create Forms",
            )
        )
        return "forms/create"
    }

    @PostMapping("/add-new")
    fun addNew(
        @ModelAttribute form: FormForm,
        model: Model
    ): String {
        try {
            val id = service.create(form)
            return "redirect:/forms/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(form, model)
        }
    }
}
