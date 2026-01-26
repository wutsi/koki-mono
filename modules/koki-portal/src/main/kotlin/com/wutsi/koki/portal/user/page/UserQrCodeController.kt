package com.wutsi.koki.portal.user.page

import com.wutsi.koki.portal.agent.service.AgentService
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/users/qr-code")
class UserQrCodeController(
    private val agentService: AgentService,
) : AbstractPageController() {
    @GetMapping
    fun qr(model: Model): String {
        val userId = userHolder.id()
        if (userId != null) {
            try {
                val agent = agentService.getByUserId(userId, false)
                if (agent.qrCodeUrl == null) {
                    model.addAttribute("generateQrCodeUrl", "/users/qr-code/generate-qr-code")
                } else {
                    model.addAttribute("publicUrl", agent.publicUrl)
                    model.addAttribute("qrCodeUrl", agent.qrCodeUrl)
                }
            } catch (e: Exception) {
                // Ignore - The user is not an agent
            }
        }

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.USER_QR_CODE,
                title = userHolder.get()?.displayName ?: ""
            )
        )
        return "users/qr-code"
    }

    @PostMapping("/generate-qr-code")
    fun generateQrCode(): String {
        val userId = userHolder.id()
        if (userId != null) {
            val agent = agentService.getByUserId(userId, false)
            agentService.generateQrCode(agent.id)
        }
        return "redirect:/users/qr-code"
    }
}
