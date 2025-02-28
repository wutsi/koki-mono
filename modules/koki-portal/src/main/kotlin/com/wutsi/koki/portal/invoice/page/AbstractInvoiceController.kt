package com.wutsi.koki.portal.invoice.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractInvoiceController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "invoice"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
