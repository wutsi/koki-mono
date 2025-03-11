package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.service.TaxService
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import com.wutsi.koki.tax.dto.TaxStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
@RequiresPermission(["tax"])
class TaxWidgetController(
    private val service: TaxService,
    private val currentUser: CurrentUserHolder,
) : AbstractPageController() {
    @GetMapping("/taxes/widget")
    fun list(model: Model): String {
        val user = currentUser.get()
        if (user != null) {
            val taxes = service.taxes(
                assigneeIds = listOf(user.id),
                statuses = listOf(
                    TaxStatus.NEW,
                    TaxStatus.PROCESSING,
                    TaxStatus.FINALIZING,
                    TaxStatus.CONTACTING,
                    TaxStatus.SUBMITTING,
                    TaxStatus.PREPARING,
                ),
                limit = 5,
            )
            if (taxes.isNotEmpty()) {
                model.addAttribute("taxes", taxes)
            }
        }
        return "taxes/widget"
    }
}
