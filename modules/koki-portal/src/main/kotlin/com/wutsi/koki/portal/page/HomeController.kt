package com.wutsi.koki.portal.page

import com.wutsi.koki.portal.model.PageModel
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController : AbstractPageController() {
    @GetMapping("/")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.HOME,
                title = "Home",
            )
        )
        return "home"
    }
}
