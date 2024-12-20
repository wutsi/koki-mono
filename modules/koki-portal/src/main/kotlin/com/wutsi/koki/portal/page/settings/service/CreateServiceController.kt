package com.wutsi.koki.portal.page.settings.service

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.model.ServiceModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.ServiceForm
import com.wutsi.koki.portal.service.ServiceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class CreateServiceController(private val service: ServiceService) : AbstractPageController() {
    @GetMapping("/settings/services/create")
    fun create(model: Model): String {
        return create(ServiceForm(), model)
    }

    private fun create(form: ServiceForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_SERVICE_CREATE,
                title = "Service"
            )
        )
        return "settings/services/create"
    }

    @PostMapping("/settings/services/add-new")
    fun addNew(
        @ModelAttribute form: ServiceForm,
        model: Model
    ): String {
        try {
            val id = service.create(form)

            val svr = ServiceModel(id = id, name = form.name, title = form.title)
            model.addAttribute("service", svr)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_SERVICE_SAVED,
                    title = svr.longTitle,
                ),
            )
            return "settings/services/saved"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(form, model)
        }
    }
}
