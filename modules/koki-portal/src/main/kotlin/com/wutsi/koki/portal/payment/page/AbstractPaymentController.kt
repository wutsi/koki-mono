package com.wutsi.koki.portal.payment.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractPaymentController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "payment"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
