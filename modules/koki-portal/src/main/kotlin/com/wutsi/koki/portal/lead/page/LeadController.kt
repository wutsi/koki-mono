package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.lead.model.LeadModel
import com.wutsi.koki.portal.security.RequiresPermission
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Date

@Controller
@RequestMapping("/leads")
@RequiresPermission(["lead", "lead:full_access"])
class ListLeadController : AbstractLeadController() {
    @GetMapping
    fun list(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LEAD_LIST,
                title = getMessage("page.lead.list.meta.title"),
            )
        )

        more(0, 20, model)
        return "leads/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        val leads = findLeads(limit, offset)
        if (leads.isNotEmpty()) {
            model.addAttribute("leads", leads)
            if (leads.size >= limit) {
                model.addAttribute(
                    "moreUrl",
                    "/leads/more?limit=$limit&offset=" + (offset + limit),
                )
            }
        }
        return "leads/more"
    }

    private fun findLeads(limit: Int, offset: Int): List<LeadModel> {
        return listOf(
            LeadModel(
                id = 111L,
                displayName = "Ray Sponsible",
                status = LeadStatus.NEW,
                createdAtText = "1 Aout 2025",
            ),
            LeadModel(
                id = 112L,
                displayName = "John Smith",
                status = LeadStatus.NEW,
                createdAtText = "30 Sept 2025",
            ),
            LeadModel(
                id = 113L,
                displayName = "Roger Milla",
                status = LeadStatus.NEW,
                createdAtText = "30 Sept 2025",
            ),
            LeadModel(
                id = 114L,
                displayName = "Thomas Nkono",
                status = LeadStatus.CONTACT_LATER,
                createdAt = DateUtils.addDays(Date(), -5),
                lastContactAtText = "10 Sept 2025",
                nextContactAtText = "1 Aout 2025",
                nextVisitAtText = "10 Sept 2025",
                createdAtText = "30 Sept 2025",
            ),
            LeadModel(
                id = 114L,
                displayName = "Omam Mbiyick",
                status = LeadStatus.VISIT_SET,
                createdAtText = "1 Jul 2025",
                nextVisitAtText = "10 Sept 2025",
            ),
        )
    }
}
