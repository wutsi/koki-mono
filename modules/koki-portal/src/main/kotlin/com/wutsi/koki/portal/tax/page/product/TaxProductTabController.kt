package com.wutsi.koki.portal.tax.page.product

import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.service.TaxProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["tax"])
class TaxProductTabController(
    private val service: TaxProductService
) : AbstractPageController() {
    @GetMapping("/tax-products/tab")
    fun list(
        @RequestParam(name = "tax-id") taxId: Long,
        @RequestParam(name = "test-mode", required = false) testMode: Boolean? = null,
        model: Model
    ): String {
        model.addAttribute("taxId", taxId)
        model.addAttribute("testMode", testMode)
        items(taxId, model)
        return "taxes/products/tab"
    }

    @GetMapping("/tax-products/tab/items")
    fun items(
        @RequestParam(name = "tax-id") taxId: Long,
        model: Model
    ): String {
        val taxProducts = service.products(taxIds = listOf(taxId), limit = Integer.MAX_VALUE)
        model.addAttribute("taxProducts", taxProducts)
        return "taxes/products/items"
    }
}
