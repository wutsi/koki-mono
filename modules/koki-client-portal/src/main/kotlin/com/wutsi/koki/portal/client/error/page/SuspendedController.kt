package com.wutsi.koki.portal.client.error.page

import com.wutsi.koki.portal.client.common.model.PageModel
import com.wutsi.koki.portal.client.common.page.AbstractPageController
import com.wutsi.koki.portal.client.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SuspendedController : AbstractPageController() {
    @GetMapping("/error/suspended")
    fun suspended(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ERROR_SUSPENDED,
                title = "Suspended"
            )
        )
        return "/error/suspended"
    }
}
