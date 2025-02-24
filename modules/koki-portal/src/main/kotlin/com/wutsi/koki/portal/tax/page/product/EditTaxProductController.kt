package com.wutsi.koki.portal.tax.page.product

import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.form.TaxProductForm
import com.wutsi.koki.portal.tax.service.TaxProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
@RequiresPermission(["tax:manage"])
class EditTaxProductController(
    private val service: TaxProductService,
    private val productService: ProductService,
) : AbstractPageController() {
    @GetMapping("/tax-products/{id}/edit")
    fun edit(@PathVariable id: Long, model: Model): String {
        val taxProduct = service.product(id)
        model.addAttribute("taxProduct", taxProduct)

        val prices = productService.prices(
            productIds = listOf(taxProduct.product.id),
            currency = tenantHolder.get()?.currency,
            limit = Integer.MAX_VALUE,
        )
        model.addAttribute("prices", prices)

        model.addAttribute(
            "form",
            TaxProductForm(
                quantity = taxProduct.quantity,
                unitPriceId = taxProduct.unitPriceId,
                description = taxProduct.description,
            )
        )
        return "taxes/products/edit"
    }

    @PostMapping("/tax-products/{id}/update")
    fun update(@PathVariable id: Long, @ModelAttribute form: TaxProductForm, model: Model): String {
        service.update(id, form)
        return "taxes/products/saved"
    }
}
