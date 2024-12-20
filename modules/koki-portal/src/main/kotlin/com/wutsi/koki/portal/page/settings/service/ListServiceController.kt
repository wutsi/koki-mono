package com.wutsi.koki.portal.page.settings.service

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.ServiceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ListServiceController(private val service: ServiceService) : AbstractPageController() {
    @GetMapping("/settings/services")
    fun list(model: Model): String {
        val services = service.services()
        if (services.isNotEmpty()) {
            model.addAttribute("services", services)
        }
        model.addAttribute(
            "page",
            PageModel(name = PageName.SETTINGS_SERVICE_LIST, title = "Services"),
        )
        return "settings/services/list"
    }
}
