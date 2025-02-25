package com.wutsi.koki.portal.tax.page.product

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.service.TaxProductService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
@RequiresPermission(["tax:manage"])
class TaxProductController(
    private val service: TaxProductService
) : AbstractPageController() {
    @GetMapping("/tax-products/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        service.delete(id)
        return "taxes/products/deleted"
    }
}
