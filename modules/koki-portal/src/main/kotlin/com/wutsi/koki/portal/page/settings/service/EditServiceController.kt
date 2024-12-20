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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class EditServiceController(private val service: ServiceService) : AbstractPageController() {
    @GetMapping("/settings/services/{id}/edit")
    fun edit(@PathVariable id: String, model: Model): String {
        val service = service.service(id)
        return edit(service, model)
    }

    private fun edit(srv: ServiceModel, model: Model): String {
        model.addAttribute("service", srv)
        model.addAttribute(
            "form",
            ServiceForm(
                name = srv.name,
                title = srv.title,
                description = srv.description ?: "",
                active = srv.active,
                authorizationType = srv.authorizationType.name,
                apiKey = srv.apiKey,
                username = srv.username,
                password = srv.password,
                baseUrl = srv.baseUrl,
            )
        )
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_SERVICE_EDIT,
                title = srv.longTitle
            )
        )
        return "settings/services/edit"
    }

    @PostMapping("/settings/services/{id}/update")
    fun update(
        @PathVariable id: String,
        @ModelAttribute form: ServiceForm,
        model: Model
    ): String {
        val srv = service.service(id)
        try {
            service.update(id, form)

            model.addAttribute("service", ServiceModel(id = id, name = form.name, title = form.title))
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_SERVICE_SAVED,
                    title = srv.longTitle,
                ),
            )
            return "settings/services/saved"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(srv, model)
        }
    }
}
