package com.wutsi.koki.portal.client.home.page

import com.wutsi.koki.portal.client.common.page.AbstractPageController
import com.wutsi.koki.portal.client.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller()
class HomeController : AbstractPageController() {
    @GetMapping("/")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.HOME,
                title = "Home",
            )
        )
        return "home/show"
    }
}
