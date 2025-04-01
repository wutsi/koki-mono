package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.service.TaxService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
@RequiresPermission(["tax:metric"])
class TaxMetricsController(
    private val service: TaxService,
) : AbstractTaxDetailsController() {
    @GetMapping("/taxes/{id}/metrics")
    fun show(
        @PathVariable id: Long,
        model: Model
    ): String {
        val tax = service.tax(id, fullGraph = false)
        model.addAttribute("tax", tax)
        return "taxes/metrics"
    }
}
