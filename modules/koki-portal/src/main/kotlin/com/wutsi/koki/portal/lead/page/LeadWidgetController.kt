package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.portal.lead.service.LeadService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/leads/widgets")
@RequiresPermission(["lead", "lead:full_access"])
class LeadWidgetController(private val service: LeadService) : AbstractLeadController() {
    @GetMapping("/new")
    fun new(
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        model: Model,
    ): String {
        model.addAttribute("testMode", testMode)

        // New Leads
        val leads = service.search(
            statuses = listOf(
                LeadStatus.NEW,
            ),
            limit = 5,
        )
        if (leads.isNotEmpty()) {
            model.addAttribute("leads", leads)
        }
        return "leads/widgets/new"
    }
}
