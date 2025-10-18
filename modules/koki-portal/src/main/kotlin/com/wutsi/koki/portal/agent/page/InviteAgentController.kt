package com.wutsi.koki.portal.agent.page

import com.wutsi.koki.portal.agent.form.AgentForm
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/agents/invite")
class InviteAgentController : AbstractAgentController() {
    @GetMapping
    fun invite(model: Model): String {
        model.addAttribute("form", AgentForm())
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.AGENT_INVITE,
                title = "Invite Agent",
            )
        )
        return "agents/invite"
    }

    @PostMapping
    fun submit(@ModelAttribute form: AgentForm, model: Model): String {
        return "redirect:/agents/invite/done?id=1111-32093420-111"
    }
}
