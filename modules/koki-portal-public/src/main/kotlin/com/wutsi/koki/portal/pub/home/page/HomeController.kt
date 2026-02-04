package com.wutsi.koki.portal.pub.home.page

import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.common.page.PageName
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController : AbstractPageController() {
    @GetMapping
    fun show(model: Model): String {
        val tenant = tenantHolder.get()
        val locale = LocaleContextHolder.getLocale()
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.HOME,
                title = getMessage("page.home.meta.title", arrayOf(tenant.name)),
                description = getMessage("page.home.meta.description", arrayOf(tenant.name), locale),
            )
        )
        return "home/show"
    }
}
