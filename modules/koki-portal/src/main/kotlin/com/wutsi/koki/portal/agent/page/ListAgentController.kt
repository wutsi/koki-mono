package com.wutsi.koki.portal.agent.page

import com.wutsi.koki.portal.agent.model.AgentModel
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/agents")
@RequiresPermission(["agent"])
class ListAgentController : AbstractAgentController() {
    @GetMapping
    fun list(model: Model): String {
        val agents = findAgents()
        model.addAttribute("agents", agents)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.AGENT_LIST,
                title = "Agents",
            )
        )
        return "agents/list"
    }

    private fun findAgents(): List<AgentModel> {
        return agentService.search(limit = 50)
    }
}
