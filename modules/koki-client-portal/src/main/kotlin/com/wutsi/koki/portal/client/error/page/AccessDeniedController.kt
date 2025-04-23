package com.wutsi.koki.portal.client.error.page

import com.wutsi.koki.portal.client.common.model.PageModel
import com.wutsi.koki.portal.client.common.page.AbstractPageController
import com.wutsi.koki.portal.client.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AccessDeniedController : AbstractPageController() {
    @GetMapping("/error/access-denied")
    fun accessDenied(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ERROR_403,
                title = "Access Denied"
            )
        )
        return "/error/403"
    }
}
