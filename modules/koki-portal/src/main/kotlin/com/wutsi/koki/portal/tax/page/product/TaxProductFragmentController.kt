package com.wutsi.koki.portal.tax.page.product

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.tax.service.TaxProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class TaxProductFragmentController(
    private val service: TaxProductService
) : AbstractPageController() {
    @GetMapping("/tax-products/{id}/fragment")
    fun delete(@PathVariable id: Long, model: Model): String {
        val taxProduct = service.product(id)
        model.addAttribute("taxProduct", taxProduct)
        return "taxes/products/fragment"
    }
}
