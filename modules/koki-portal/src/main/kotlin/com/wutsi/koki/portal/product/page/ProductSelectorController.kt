package com.wutsi.koki.portal.product.page

import com.wutsi.koki.portal.product.service.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductSelectorController(
    private val service: ProductService
) {
    @GetMapping("/products/selector/search")
    fun new(
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): List<Map<String, Any>> {
        return service.products(
            keyword = keyword,
            limit = limit,
            offset = offset
        ).map { product ->
            mapOf(
                "id" to product.id,
                "name" to product.name,
            )
        }
    }
}
