package com.wutsi.koki.portal.invoice.page

import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController

abstract class AbstractDetailsInvoiceController : AbstractModuleDetailsPageController() {
    override fun getModuleName(): String {
        return AbstractInvoiceController.MODULE_NAME
    }
}
