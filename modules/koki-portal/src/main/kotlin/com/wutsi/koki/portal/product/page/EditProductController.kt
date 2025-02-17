package com.wutsi.koki.portal.product.page

import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.product.form.ProductForm
import com.wutsi.koki.portal.product.model.ProductModel
import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.refdata.service.CategoryService
import com.wutsi.koki.portal.refdata.service.UnitService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.product.dto.ProductType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["product:manage"])
class EditProductController(
    private val service: ProductService,
    private val unitService: UnitService,
    private val categoryService: CategoryService,
) : AbstractProductController() {
    @GetMapping("/products/{id}/edit")
    fun edit(@PathVariable id: Long, model: Model): String {
        val product = service.product(id)
        val form = ProductForm(
            name = product.name,
            code = product.code,
            description = product.description,
            type = product.type,
            active = product.active,
            quantity = product.serviceDetails?.quantity,
            unitId = product.serviceDetails?.unit?.id,
            categoryId = product.category?.id,
        )
        return edit(product, form, model)
    }

    private fun edit(product: ProductModel, form: ProductForm, model: Model): String {
        model.addAttribute("product", product)
        model.addAttribute("form", form)
        model.addAttribute("types", ProductType.entries.filter { entry -> entry != ProductType.UNKNOWN })
        model.addAttribute("units", unitService.units())
        model.addAttribute("category", form.categoryId?.let { id -> categoryService.category(id) })

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.PRODUCT_EDIT,
                title = product.name,
            )
        )
        return "products/edit"
    }

    @PostMapping("/products/{id}/update")
    fun update(@PathVariable id: Long, @ModelAttribute form: ProductForm, model: Model): String {
        try {
            service.update(id, form)
            return "redirect:/products/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            val product = service.product(id)
            model.addAttribute("error", errorResponse.error.code)
            return edit(product, form, model)
        }
    }
}
