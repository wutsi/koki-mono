package com.wutsi.koki.room.web.home.page

import com.wutsi.koki.room.web.common.page.AbstractPageController
import com.wutsi.koki.room.web.common.page.PageName
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.Locale

@Controller
class HomeController : AbstractPageController() {
    @GetMapping
    fun show(model: Model): String {
        val tenant = tenantHolder.get()!!
        val locale = LocaleContextHolder.getLocale()
        val country = Locale(locale.language, tenant.country)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.HOME,
                title = getMessage(
                    "page.home.html.title",
                    arrayOf(tenant.name, country.getDisplayCountry(locale)),
                    locale
                ),
                description = getMessage("page.home.html.description", arrayOf(tenant.name), locale),
            )
        )
        return "home/show"
    }
}
