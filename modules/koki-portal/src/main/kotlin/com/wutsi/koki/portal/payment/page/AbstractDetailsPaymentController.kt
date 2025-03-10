package com.wutsi.koki.portal.payment.page

import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController

abstract class AbstractDetailsPaymentController : AbstractModuleDetailsPageController() {
    override fun getModuleName(): String {
        return AbstractPaymentController.MODULE_NAME
    }
}
