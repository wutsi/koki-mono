package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.model.TaxModel
import com.wutsi.koki.portal.tax.service.TaxService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["tax"])
class TaxController(
    private val service: TaxService,
) : AbstractTaxDetailsController() {
    @GetMapping("/taxes/{id}")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @PathVariable id: Long,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        val tax = service.tax(id)
        if (
            toast == id &&
            canShowToasts(timestamp, referer, listOf("/taxes/$id/edit", "/taxes/$id/status", "/taxes/create"))
        ) {
            model.addAttribute("toast", "Saved")
        }
        return show(tax, model)
    }

    fun show(tax: TaxModel, model: Model): String {
        model.addAttribute("tax", tax)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TAX,
                title = tax.name,
            )
        )
        return "taxes/show"
    }

    @GetMapping("/taxes/{id}/delete")
    @RequiresPermission(["tax:delete"])
    fun delete(@PathVariable id: Long, model: Model): String {
        val tax = service.tax(id)
        try {
            service.delete(id)
            return "redirect:/taxes?_op=del&_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(tax, model)
        }
    }
}
