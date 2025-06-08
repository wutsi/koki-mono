package com.wutsi.koki.portal.tenant.page.settings.type

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.BusinessService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["tenant:admin"])
class SettingsBusinessController(
    private val service: BusinessService
) : AbstractPageController() {
    @GetMapping("/settings/tenant/business")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model,
    ): String {
        val business = try {
            service.business()
        } catch (ex: Exception) {
            null
        }
        model.addAttribute("business", business)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TENANT_SETTINGS_BUSINESS,
                title = "Business",
            )
        )

        if (
            toast != null &&
            canShowToasts(timestamp, referer, listOf("/settings/tenant/business/edit"))
        ) {
            model.addAttribute("toast", "Saved")
        }

        return "tenant/settings/business/show"
    }
}
