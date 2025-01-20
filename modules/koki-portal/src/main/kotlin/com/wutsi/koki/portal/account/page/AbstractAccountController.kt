package com.wutsi.koki.portal.account.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractAccountController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "account"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
