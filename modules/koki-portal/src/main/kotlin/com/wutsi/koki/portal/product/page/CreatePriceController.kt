package com.wutsi.koki.portal.product.page

import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.product.form.PriceForm
import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Currency

@Controller
@RequiresPermission(["product:manage"])
class CreatePriceController(private val service: ProductService) : AbstractPageController() {
    @GetMapping("/prices/create")
    fun create(@RequestParam(name = "product-id") productId: Long, model: Model): String {
        val currency = Currency.getInstance(tenantHolder.get()!!.currency)
        model.addAttribute("currencies", listOf(currency))

        val form = PriceForm(
            productId = productId,
            currency = currency.currencyCode,
        )
        model.addAttribute("form", form)

        return "products/prices/create"
    }

    @PostMapping("/prices/add-new")
    fun addNew(@ModelAttribute form: PriceForm, model: Model): String {
        service.create(form)
        return "products/prices/saved"
    }
}
