package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractLeadController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "lead"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
