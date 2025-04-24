package com.wutsi.koki.portal.client.tax.page

import com.wutsi.koki.portal.client.common.page.AbstractPageController
import com.wutsi.koki.portal.client.common.page.PageName
import com.wutsi.koki.portal.client.security.RequiresModule
import com.wutsi.koki.portal.client.tax.service.TaxService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/taxes")
@RequiresModule("tax")
class ListTaxController(
    private val service: TaxService,
) : AbstractPageController() {
    @GetMapping()
    fun list(model: Model): String {
        more(
            limit = 20,
            offset = 0,
            model = model
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TAX_LIST,
                title = "Taxes",
            )
        )

        return "taxes/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false) limit: Int,
        @RequestParam(required = false) offset: Int,
        model: Model
    ): String {
        val taxes = service.taxes(
            limit = limit,
            offset = offset,
        )
        if (taxes.isNotEmpty()) {
            model.addAttribute("taxes", taxes)

            if (taxes.size >= limit) {
                val nextOffset = offset + limit
                var url = "/taxes/more?limit=$limit&offset=$nextOffset"
                model.addAttribute("moreUrl", url)
            }
        }

        return "taxes/more"
    }
}
