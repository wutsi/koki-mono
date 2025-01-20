package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController

abstract class AbstractContactDetailsController : AbstractModuleDetailsPageController() {
    override fun getModuleName(): String {
        return AbstractContactController.MODULE_NAME
    }
}
