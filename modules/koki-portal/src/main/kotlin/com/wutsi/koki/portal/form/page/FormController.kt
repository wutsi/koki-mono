package com.wutsi.koki.portal.form.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.form.model.FormModel
import com.wutsi.koki.portal.form.service.FormService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/forms")
@RequiresPermission(["form"])
class FormController(
    private val service: FormService
) : AbstractFormDetailsController() {
    @GetMapping("/{id}")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @PathVariable id: Long,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model,
    ): String {
        val form = service.form(id)
        loadToast(id, referer, toast, timestamp, model)
        return show(form, model)
    }

    @RequiresPermission(permissions = ["form:delete"])
    @GetMapping("/{id}/delete")
    fun delete(@PathVariable id: Long, model: Model): String {
        try {
            service.delete(id)
            return "redirect:/forms?_op=del&_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(id = id, model = model)
        }
    }

    private fun show(form: FormModel, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page", createPageModel(
                name = PageName.FORM,
                title = form.name,
            )
        )
        return "forms/show"
    }

    private fun loadToast(
        id: Long, referer: String?, toast: Long?, timestamp: Long?, model: Model
    ) {
        if (toast == id && canShowToasts(timestamp, referer, listOf("/forms/$id/edit", "/forms/create"))) {
            model.addAttribute("toast", "Saved")
        }
    }
}
