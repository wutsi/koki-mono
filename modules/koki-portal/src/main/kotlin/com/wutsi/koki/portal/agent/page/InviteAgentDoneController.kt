package com.wutsi.koki.portal.agent.page

import com.wutsi.koki.portal.agent.model.InvitationModel
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/agents/invite/done")
class InviteAgentDoneController : AbstractAgentController() {
    @GetMapping
    fun done(@RequestParam id: String, model: Model): String {
        model.addAttribute("invitation", findInvitation(id))
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.AGENT_INVITE_DONE,
                title = "Invite Agent",
            )
        )
        return "agents/invite-done"
    }

    private fun findInvitation(id: String): InvitationModel {
        return InvitationModel(
            id = id,
            email = "ray.sponsible@gmail.com",
            displayName = "Ray Sponsible",
        )
    }
}
