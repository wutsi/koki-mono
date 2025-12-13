package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractLeadMessageController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "lead_message"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
