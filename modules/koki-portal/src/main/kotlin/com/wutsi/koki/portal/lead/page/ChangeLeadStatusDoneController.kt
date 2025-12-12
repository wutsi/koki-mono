package com.wutsi.koki.portal.lead.page

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

@Controller
@RequestMapping("/leads/status/done")
@RequiresPermission(["lead", "lead:full_access"])
class ChangeLeadStatusDoneController : AbstractLeadDetailsController() {
    @GetMapping
    fun status(@RequestParam id: Long, model: Model): String {
        val lead = findLead(id)
        model.addAttribute("lead", lead)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LEAD_STATUS_DONE,
                title = lead.user.displayName ?: "",
            )
        )
        return "leads/status-done"
    }

    @PostMapping
    fun submit(@ModelAttribute form: LeadForm): String {
        service.updateStatus(form)
        return "redirect:/leads/${form.id}"
    }
}
