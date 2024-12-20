package com.wutsi.koki.portal.page.settings.service

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.model.ServiceModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.ServiceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.client.HttpClientErrorException

@Controller
class ShowServiceController(private val service: ServiceService) : AbstractPageController() {
    @GetMapping("/settings/services/{id}")
    fun show(@PathVariable id: String, model: Model): String {
        val service = service.service(id)
        return show(service, model)
    }

    private fun show(service: ServiceModel, model: Model): String {
        model.addAttribute("service", service)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_SERVICE,
                title = service.longTitle
            )
        )
        return "settings/services/show"
    }

    @GetMapping("/settings/services/{id}/delete")
    fun delete(@PathVariable id: String, model: Model): String {
        val srv = service.service(id)
        try {
            service.delete(id)

            model.addAttribute("service", srv)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_SERVICE_DELETED,
                    title = srv.longTitle
                ),
            )
            return "settings/services/deleted"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(srv, model)
        }
    }
}
