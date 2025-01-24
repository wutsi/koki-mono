package com.wutsi.koki.portal.tax.page.settings.type

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.tax.service.TaxTypeService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class SettingsTaxTypeController(private val service: TaxTypeService) : AbstractPageController() {
    @GetMapping("/settings/taxes/types/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val type = service.taxType(id)
        model.addAttribute("type", type)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.TAX_SETTINGS_TYPE,
                title = type.name,
            )

        )
        return "taxes/settings/types/show"
    }
}
