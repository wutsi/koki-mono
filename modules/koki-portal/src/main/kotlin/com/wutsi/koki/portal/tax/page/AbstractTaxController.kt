package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController
import org.springframework.ui.Model
import java.time.LocalDate

abstract class AbstractTaxController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "tax"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }

    protected fun loadFiscalYears(model: Model, currentYear: Int? = null) {
        val year2 = LocalDate.now().year - 1
        model.addAttribute("years", (year2 downTo (year2 - 100)).toList())
        model.addAttribute("currentYear", currentYear)
    }
}
