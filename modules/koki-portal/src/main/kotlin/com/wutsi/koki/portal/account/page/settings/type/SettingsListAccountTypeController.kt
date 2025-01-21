package com.wutsi.koki.portal.account.page.settings.type

import com.wutsi.koki.portal.account.service.AccountTypeService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class SettingsListAccountTypeController(private val service: AccountTypeService) : AbstractPageController() {
    @GetMapping("/settings/accounts/types")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ACCOUNT_SETTINGS_TYPE_LIST,
                title = "Account Types",
            )

        )
        more(model = model)
        return "accounts/settings/types/list"
    }

    @GetMapping("/settings/accounts/types/more")
    fun more(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val types = service.accountTypes(limit = limit, offset = offset)
        model.addAttribute("types", types)
        if (types.size >= limit) {
            val nextOffset = offset + limit
            val moreUrl = "/settings/accounts/types/more?limit=$limit&offset=$nextOffset"
            model.addAttribute("moreUrl", moreUrl)
        }
        return "accounts/settings/types/more"
    }
}
