package com.wutsi.koki.portal.account.page

import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController

abstract class AbstractAccountDetailsController : AbstractModuleDetailsPageController() {
    override fun getModuleName(): String {
        return AbstractAccountController.MODULE_NAME
    }
}
