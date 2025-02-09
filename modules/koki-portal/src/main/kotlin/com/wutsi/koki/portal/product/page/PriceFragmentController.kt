package com.wutsi.koki.portal.product.page

import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
@RequiresPermission(["product"])
class PriceFragmentController(private val service: ProductService) : AbstractProductController() {
    @GetMapping("/prices/{id}/fragment")
    fun list(
        @PathVariable id: Long,
        model: Model,
    ): String {
        val price = service.price(id)
        model.addAttribute("price", price)
        return "products/prices/fragment"
    }
}
