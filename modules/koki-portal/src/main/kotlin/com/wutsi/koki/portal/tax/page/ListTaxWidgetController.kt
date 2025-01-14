package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.tax.service.TaxService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ListTaxWidgetController(private val service: TaxService) : AbstractPageController() {
    @GetMapping("/taxes/widgets/list")
    fun list(
        @RequestParam(required = false) title: String? = null,
        @RequestParam(required = false, name = "load-more") loadMore: Boolean = true,
        @RequestParam(required = false, name = "account-id") accountId: Long? = null,
        @RequestParam(required = false, name = "participant-id") participantId: Long? = null,
        @RequestParam(required = false, name = "assignee-id") assigneeId: Long? = null,
        @RequestParam(required = false, name = "show-account") showAccount: Boolean = true,
        @RequestParam(required = false, name = "small-view") smallView: Boolean = false,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        more(
            accountId = accountId,
            participantId = participantId,
            assigneeId = assigneeId,
            smallView = smallView,
            limit = limit,
            offset = offset,
            model = model
        )

        model.addAttribute("title", title)
        model.addAttribute("loadMore", loadMore)
        model.addAttribute("showAccount", showAccount)
        return "taxes/widgets/list"
    }

    @GetMapping("/taxes/widgets/list/more")
    fun more(
        @RequestParam(required = false, name = "account-id") accountId: Long? = null,
        @RequestParam(required = false, name = "participant-id") participantId: Long? = null,
        @RequestParam(required = false, name = "assignee-id") assigneeId: Long? = null,
        @RequestParam(required = false, name = "small-view") smallView: Boolean = false,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val taxes = service.taxes(
            accountIds = accountId?.let { id -> listOf(id) } ?: emptyList(),
            participantIds = participantId?.let { id -> listOf(id) } ?: emptyList(),
            assigneeIds = assigneeId?.let { id -> listOf(id) } ?: emptyList(),
            limit = limit,
            offset = offset,
        )
        if (taxes.isNotEmpty()) {
            model.addAttribute("taxes", taxes)
            model.addAttribute("smallView", smallView)

            if (taxes.size >= limit) {
                val nextOffset = offset + limit
                val url = "/taxes/widgets/list/more?limit=$limit&offset=$nextOffset" +
                    (accountId?.let { id -> "&account-id=$id" } ?: "") +
                    (participantId?.let { id -> "&participant-id=$id" } ?: "") +
                    (assigneeId?.let { id -> "&assignee-id=$id" } ?: "")
                model.addAttribute("moreUrl", url)
            }
        }

        return "taxes/more"
    }
}
