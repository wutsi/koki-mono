package com.wutsi.koki.portal.account.page.settings.attribute

import com.wutsi.koki.portal.account.service.AttributeService
import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(permissions = ["account:admin"])
class SettingsListAttributeController(private val service: AttributeService) : AbstractPageController() {
    @GetMapping("/settings/accounts/attributes")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ACCOUNT_SETTINGS_ATTRIBUTE_LIST,
                title = "Account Attributes",
            )

        )
        more(model = model)
        return "accounts/settings/attributes/list"
    }

    @GetMapping("/settings/accounts/attributes/more")
    fun more(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val attributes = service.attributes(limit = limit, offset = offset)
        model.addAttribute("attributes", attributes)
        if (attributes.size >= limit) {
            val nextOffset = offset + limit
            val moreUrl = "/settings/accounts/attributes/more?limit=$limit&offset=$nextOffset"
            model.addAttribute("moreUrl", moreUrl)
        }
        return "accounts/settings/attributes/more"
    }
}
