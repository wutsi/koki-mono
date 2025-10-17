package com.wutsi.koki.portal.agent.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractAgentController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "agent"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
