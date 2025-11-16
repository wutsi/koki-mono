package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/leads")
@RequiresPermission(["lead", "lead:full_access"])
class LeadController : AbstractLeadDetailsController() {
    @GetMapping("/{id}")
    fun list(@PathVariable id: Long, model: Model): String {
        val lead = findLead(id)
        model.addAttribute("lead", lead)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LEAD,
                title = lead.displayName,
            )
        )
        return "leads/show"
    }
}
