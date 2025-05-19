package com.wutsi.koki.room.web.home.page

import com.wutsi.koki.room.web.common.page.AbstractPageController
import com.wutsi.koki.room.web.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController : AbstractPageController() {
    @GetMapping
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.HOME,
                "home",
            )
        )
        return "home/show"
    }
}
