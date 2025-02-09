package com.wutsi.koki.portal.product.page

import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.product.dto.ProductType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["product"])
class ListProductController(private val service: ProductService) : AbstractProductController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ListProductController::class.java)
    }

    @GetMapping("/products")
    fun list(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false) type: ProductType? = null,
        @RequestParam(required = false) active: Int? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.PRODUCT_LIST,
                title = "Products",
            )
        )

        model.addAttribute("active", active)

        model.addAttribute("type", type)
        model.addAttribute("types", ProductType.entries.filter { entry -> entry != ProductType.UNKNOWN })

        loadToast(referer, toast, timestamp, model)
        more(type, active, limit, offset, model)
        return "products/list"
    }

    @GetMapping("/products/more")
    fun more(
        @RequestParam(required = false) type: ProductType? = null,
        @RequestParam(required = false) active: Int? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val products = service.products(
            types = type?.let { listOf(type) } ?: emptyList(),
            active = active?.let { value -> if (value == 0) false else true },
            limit = limit,
            offset = offset
        )
        if (products.isNotEmpty()) {
            model.addAttribute("products", products)
            if (products.size >= limit) {
                val nextOffset = offset + limit
                var url = "/products/more?limit=$limit&offset=$nextOffset"
                if (type != null) {
                    url = "$url&type=$type"
                }
                if (active != null) {
                    url = "$url&active=$active"
                }
                model.addAttribute("moreUrl", url)
            }
        }

        return "products/more"
    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/products/$toast", "/products/create"))) {
            try {
                val product = service.product(toast)
                model.addAttribute(
                    "toast",
                    "<a href='/products/${product.id}'>${product.name}</a> has been saved!"
                )
            } catch (ex: Exception) {
                LOGGER.warn("Unable to load toast information for Product#$toast", ex)
            }
        }
    }
}
