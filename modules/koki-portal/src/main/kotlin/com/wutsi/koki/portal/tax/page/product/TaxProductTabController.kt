package com.wutsi.koki.portal.tax.page.product

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.service.TaxProductService
import com.wutsi.koki.portal.tax.service.TaxService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["tax"])
class TaxProductTabController(
    private val service: TaxProductService,
    private val taxService: TaxService,
) : AbstractPageController() {
    @GetMapping("/tax-products/tab")
    fun list(
        @RequestParam(name = "tax-id") taxId: Long,
        @RequestParam(name = "test-mode", required = false) testMode: Boolean? = null,
        @RequestParam(name = "read-only", required = false) readOnly: Boolean? = null,
        model: Model
    ): String {
        val tax = taxService.tax(taxId)
        model.addAttribute("tax", tax)

        model.addAttribute("testMode", testMode)
        items(taxId, readOnly, model)
        return "taxes/products/tab"
    }

    @GetMapping("/tax-products/tab/items")
    fun items(
        @RequestParam(name = "tax-id") taxId: Long,
        @RequestParam(name = "read-only", required = false) readOnly: Boolean? = null,
        model: Model
    ): String {
        val taxProducts = service.products(taxIds = listOf(taxId), limit = Integer.MAX_VALUE)
        model.addAttribute("taxProducts", taxProducts)
        model.addAttribute("readOnly", readOnly)
        return "taxes/products/items"
    }
}
