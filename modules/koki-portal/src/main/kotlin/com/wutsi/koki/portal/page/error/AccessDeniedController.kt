package com.wutsi.koki.portal.page.error

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class AccessDeniedController : AbstractPageController() {
    @GetMapping("/error/access-denied")
    fun error(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ERROR_ACCESS_DENIED,
                title = "Access Denied"
            )
        )
        return "error/access-denied"
    }
}
