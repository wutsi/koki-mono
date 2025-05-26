package com.wutsi.koki.portal.product.page

import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["product"])
class PriceTabController(private val service: ProductService) : AbstractProductController() {
    @GetMapping("/prices/tab")
    fun list(
        @RequestParam(required = false, name = "product-id") productId: Long,
        @RequestParam(name = "test-mode", required = false) testMode: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        model.addAttribute("productId", productId)
        model.addAttribute("testMode", testMode)
        more(productId, limit, offset, model)
        return "products/prices/tab"
    }

    @GetMapping("/prices/tab/more")
    fun more(
        @RequestParam(required = false, name = "product-id") productId: Long,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        val prices = service.prices(
            productIds = listOf(productId),
            limit = limit,
            offset = offset
        )
        if (prices.isNotEmpty()) {
            model.addAttribute("prices", prices)
            if (prices.size >= limit) {
                val nextOffset = offset + limit
                var url = "/prices/tab/more.html?product-id=$productId&limit=$limit&offset=$nextOffset"
                model.addAttribute("moreUrl", url)
            }
        }

        return "products/prices/more"
    }
}
