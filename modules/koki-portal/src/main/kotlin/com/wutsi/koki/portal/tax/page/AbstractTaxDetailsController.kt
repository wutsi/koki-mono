package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController

abstract class AbstractTaxDetailsController : AbstractModuleDetailsPageController() {
    override fun getModuleName(): String {
        return AbstractTaxController.MODULE_NAME
    }
}
