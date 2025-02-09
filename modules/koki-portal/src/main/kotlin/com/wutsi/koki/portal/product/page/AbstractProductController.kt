package com.wutsi.koki.portal.product.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractProductController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "product"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
