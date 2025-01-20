package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractTaxController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "tax"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
