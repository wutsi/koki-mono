package com.wutsi.koki.portal.tax.page.product

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.form.TaxProductForm
import com.wutsi.koki.portal.tax.service.TaxProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["tax:manage"])
class CreateTaxProductController(
    private val service: TaxProductService,
    private val productService: ProductService,
) : AbstractPageController() {
    @GetMapping("/tax-products/create")
    fun create(@RequestParam("tax-id") taxId: Long, model: Model): String {
        model.addAttribute("form", TaxProductForm(taxId = taxId))
        return "taxes/products/create"
    }

    @GetMapping("/tax-products/prices")
    fun prices(@RequestParam("product-id") productId: Long, model: Model): String {
        model.addAttribute(
            "prices",
            productService.prices(
                productIds = listOf(productId),
                limit = Integer.MAX_VALUE
            )
        )
        return "taxes/products/prices"
    }

    @PostMapping("/tax-products/add-new")
    fun addNew(@ModelAttribute form: TaxProductForm, model: Model): String {
        service.add(form)
        return "taxes/products/saved"
    }
}
