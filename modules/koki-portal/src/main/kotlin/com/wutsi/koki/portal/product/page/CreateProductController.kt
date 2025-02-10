package com.wutsi.koki.portal.product.page

import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.product.form.ProductForm
import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.refdata.service.UnitService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.product.dto.ProductType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["product:manage"])
class CreateProductController(
    private val service: ProductService,
    private val unitService: UnitService,
) : AbstractProductController() {
    @GetMapping("/products/create")
    fun create(model: Model): String {
        val form = ProductForm()
        return create(form, model)
    }

    private fun create(form: ProductForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute("types", ProductType.entries.filter { entry -> entry != ProductType.UNKNOWN })
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.PRODUCT_CREATE,
                title = "Products",
            )
        )

        model.addAttribute("units", unitService.units())

        return "products/create"
    }

    @PostMapping("/products/add-new")
    fun addNew(@ModelAttribute form: ProductForm, model: Model): String {
        try {
            val id = service.create(form)
            return "redirect:/products?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(form, model)
        }
    }
}
