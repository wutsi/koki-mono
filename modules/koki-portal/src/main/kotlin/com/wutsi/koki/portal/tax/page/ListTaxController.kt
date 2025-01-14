package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.CurrentUserHolder
import com.wutsi.koki.portal.tax.service.TaxService
import com.wutsi.koki.tax.dto.TaxStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ListTaxController(
    private val service: TaxService,
    private val currentUser: CurrentUserHolder,
) : AbstractPageController() {
    companion object {
        const val COL_ALL_REPORTS = "1"
        const val COL_MY_REPORTS = "2"
        const val COL_MY_ASSIGNED_REPORTS = "3"
        const val COL_MY_DONE_REPORTS = "4"
        const val COL_ALL_DONE_REPORTS = "5"
    }

    @GetMapping("/taxes")
    fun list(
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        more(
            collection = collection,
            limit = limit,
            offset = offset,
            model = model
        )

        model.addAttribute("collection", toCollection(collection))
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.TAX_LIST,
                title = "Taxes",
            )
        )
        return "taxes/list"
    }

    @GetMapping("/taxes/more")
    fun more(
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val col = toCollection(collection)
        val userId = currentUser.id()
        val taxes = service.taxes(
            participantIds = if (col == COL_MY_REPORTS || col == COL_MY_DONE_REPORTS) {
                userId?.let { id -> listOf(id) } ?: emptyList()
            } else {
                emptyList()
            },
            assigneeIds = if (col == COL_MY_ASSIGNED_REPORTS) {
                userId?.let { id -> listOf(id) } ?: emptyList()
            } else {
                emptyList()
            },
            statuses = if (col == COL_MY_DONE_REPORTS || col == COL_ALL_DONE_REPORTS) {
                listOf(TaxStatus.DONE)
            } else {
                TaxStatus.values().filter { status -> status != TaxStatus.DONE }
            },
            limit = limit,
            offset = offset,
        )
        if (taxes.isNotEmpty()) {
            model.addAttribute("taxes", taxes)
            model.addAttribute("showAccount", true)

            if (taxes.size >= limit) {
                val nextOffset = offset + limit
                var url = "/taxes/more?limit=$limit&offset=$nextOffset"
                if (collection != null) {
                    url = "$url&col=$collection"
                }
                model.addAttribute("moreUrl", url)
            }
        }

        return "taxes/more"
    }

    private fun toCollection(collection: String?): String {
        return when (collection) {
            COL_ALL_REPORTS -> COL_ALL_REPORTS
            COL_MY_REPORTS -> COL_MY_REPORTS
            COL_MY_ASSIGNED_REPORTS -> COL_MY_ASSIGNED_REPORTS
            COL_ALL_DONE_REPORTS -> COL_ALL_DONE_REPORTS
            COL_MY_DONE_REPORTS -> COL_MY_DONE_REPORTS
            else -> COL_ALL_REPORTS
        }
    }
}
