package com.wutsi.koki.portal.tax.page.settings.type

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.service.TaxTypeService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["tax:admin"])
class SettingsListTaxTypeController(private val service: TaxTypeService) : AbstractPageController() {
    @GetMapping("/settings/taxes/types")
    fun list(
        model: Model
    ): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.TAX_SETTINGS_TYPE_LIST,
                title = "Tax Types",
            )

        )
        more(model = model)
        return "taxes/settings/types/list"
    }

    @GetMapping("/settings/taxes/types/more")
    fun more(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val types = service.taxTypes(limit = limit, offset = offset)
        model.addAttribute("types", types)
        if (types.size >= limit) {
            val nextOffset = offset + limit
            val moreUrl = "/settings/taxes/types/more?limit=$limit&offset=$nextOffset"
            model.addAttribute("moreUrl", moreUrl)
        }
        return "taxes/settings/types/more"
    }
}
