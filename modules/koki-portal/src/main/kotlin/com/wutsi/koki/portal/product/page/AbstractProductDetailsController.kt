package com.wutsi.koki.portal.product.page

import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController

abstract class AbstractProductDetailsController : AbstractModuleDetailsPageController() {
    override fun getModuleName(): String {
        return AbstractProductController.MODULE_NAME
    }
}
