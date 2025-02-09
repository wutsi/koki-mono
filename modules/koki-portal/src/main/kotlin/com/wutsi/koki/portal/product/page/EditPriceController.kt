package com.wutsi.koki.portal.product.page

import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.product.form.PriceForm
import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.text.SimpleDateFormat
import java.util.Currency

@Controller
@RequiresPermission(["product:manage"])
class EditPriceController(private val service: ProductService) : AbstractPageController() {
    @GetMapping("/prices/{id}/edit")
    fun edit(@PathVariable id: Long, model: Model): String {
        val currency = Currency.getInstance(tenantHolder.get()!!.currency)
        model.addAttribute("currencies", listOf(currency))

        val price = service.price(id)
        model.addAttribute("price", price)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val form = PriceForm(
            name = price.name,
            amount = price.amount.value,
            currency = price.amount.currency,
            active = price.active,
            startAt = price.startAt?.let { date -> fmt.format(date) },
            endAt = price.endAt?.let { date -> fmt.format(date) },
        )
        model.addAttribute("form", form)

        return "products/prices/edit"
    }

    @PostMapping("/prices/{id}/update")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: PriceForm,
        model: Model
    ): String {
        service.update(id, form)
        return "products/prices/saved"
    }
}
