package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.lead.form.LeadForm
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.text.SimpleDateFormat

@Controller
@RequestMapping("/leads/status")
@RequiresPermission(["lead", "lead:full_access"])
class ChangeLeadStatusController : AbstractLeadDetailsController() {
    @GetMapping
    fun status(@RequestParam id: Long, model: Model): String {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
        val lead = findLead(id)
        model.addAttribute("lead", lead)
        model.addAttribute("statuses", LeadStatus.entries.filter { status -> status != LeadStatus.UNKNOWN })
        model.addAttribute(
            "form",
            LeadForm(
                id = id,
                status = lead.status,
                nextContactAt = lead.nextContactAt?.let { date -> df.format(date) },
                nextVisitAt = (lead.nextVisitAt ?: lead.visitRequestedAt)?.let { date -> df.format(date) }
            )
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LEAD_STATUS,
                title = lead.displayName,
            )
        )
        return "leads/status"
    }

    @PostMapping
    fun submit(@ModelAttribute form: LeadForm): String {
        service.updateStatus(form)
        return "redirect:/leads/status/done?id=${form.id}"
    }
}
