package com.wutsi.koki.portal.agent.page

import com.wutsi.koki.portal.agent.service.AgentService
import com.wutsi.koki.portal.module.page.AbstractModulePageController
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractAgentController : AbstractModulePageController() {
    @Autowired
    protected lateinit var agentService: AgentService

    companion object {
        const val MODULE_NAME = "agent"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
