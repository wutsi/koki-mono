package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.lead.model.LeadModel
import com.wutsi.koki.portal.lead.service.LeadService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLEncoder

@Controller
@RequestMapping("/leads")
@RequiresPermission(["lead", "lead:full_access"])
class ListLeadController(private val service: LeadService) : AbstractLeadController() {
    @GetMapping
    fun list(
        @RequestParam(required = false, name = "q") keywords: String? = null,
        model: Model,
    ): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LEAD_LIST,
                title = getMessage("page.lead.list.meta.title"),
            )
        )

        model.addAttribute("keywords", keywords)
        more(keywords, 20, 0, model)
        return "leads/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false, name = "q") keywords: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        val leads = findLeads(keywords, limit, offset)
        if (leads.isNotEmpty()) {
            model.addAttribute("leads", leads)
            if (leads.size >= limit) {
                model.addAttribute(
                    "moreUrl",
                    "/leads/more?limit=$limit&offset=" + (offset + limit) +
                        (keywords?.trim()?.ifEmpty { null }?.let { q -> "&q=" + URLEncoder.encode(q, "UTF-8") } ?: ""),
                )
            }
        }
        return "leads/more"
    }

    private fun findLeads(keywords: String?, limit: Int, offset: Int): List<LeadModel> {
        return service.search(
            agentUserIds = listOf(userHolder.id() ?: -1),
            keywords = keywords,
            limit = limit,
            offset = offset,
        )
    }
}
