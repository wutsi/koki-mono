package com.wutsi.koki.portal.account.page.settings.type

import com.wutsi.koki.portal.account.service.AccountTypeService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class SettingsAccountTypeController(private val service: AccountTypeService) : AbstractPageController() {
    @GetMapping("/settings/accounts/types/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val type = service.accountType(id)
        model.addAttribute("type", type)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ACCOUNT_SETTINGS_TYPE,
                title = type.name,
            )

        )
        return "accounts/settings/types/show"
    }
}
