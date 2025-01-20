package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractContactController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "contact"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
