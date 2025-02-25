package com.wutsi.koki.portal.product.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.product.model.ProductModel
import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["product"])
class ProductController(private val service: ProductService) : AbstractProductDetailsController() {
    @GetMapping("/products/{id}")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @PathVariable id: Long,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        val product = service.product(id)
        loadToast(id, referer, toast, timestamp, model)
        return show(product, model)
    }

    private fun show(product: ProductModel, model: Model): String {
        model.addAttribute("product", product)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.PRODUCT,
                title = product.name,
            )
        )
        return "products/show"
    }

    @GetMapping("/products/{id}/delete")
    @RequiresPermission(["product:delete"])
    fun delete(@PathVariable id: Long, model: Model): String {
        try {
            service.delete(id)
            return "redirect:/products?_op=del&_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            val product = service.product(id)
            model.addAttribute("error", errorResponse.error.code)
            return show(product, model)
        }
    }

    private fun loadToast(
        id: Long,
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        model: Model
    ) {
        if (toast == id && canShowToasts(timestamp, referer, listOf("/products/$id/edit", "/accounts/product"))) {
            model.addAttribute("toast", "Saved")
        }
    }
}
