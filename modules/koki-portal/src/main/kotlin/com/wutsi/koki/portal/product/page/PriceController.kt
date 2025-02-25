package com.wutsi.koki.portal.product.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class PriceController(private val service: ProductService) : AbstractPageController() {
    @GetMapping("/prices/{id}/delete")
    @RequiresPermission(["product:manage"])
    fun addNew(@PathVariable id: Long, model: Model): String {
        service.deletePrice(id)
        return "products/prices/saved"
    }
}
