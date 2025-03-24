package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.service.TaxService
import com.wutsi.koki.portal.tenant.service.TypeService
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import com.wutsi.koki.tax.dto.TaxStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Controller
@RequiresPermission(["tax"])
class ListTaxController(
    private val service: TaxService,
    private val currentUser: CurrentUserHolder,
    private val typeService: TypeService,
) : AbstractTaxController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ListTaxController::class.java)

        const val COL_ALL_REPORTS = "1"
        const val COL_MY_REPORTS = "2"
        const val COL_MY_ASSIGNED_REPORTS = "3"

        const val VIEW_TABLE = "1"
        const val VIEW_CALENDAR = "2"
    }

    @GetMapping("/taxes")
    fun list(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false, name = "view") view: String? = null,
        @RequestParam(required = false, name = "month") month: String? = null,
        @RequestParam(required = false, name = "fiscal-year") fiscalYear: Int? = null,
        @RequestParam(required = false, name = "type-id") typeId: Long? = null,
        @RequestParam(required = false) status: TaxStatus? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        @RequestParam(required = false, name = "_op") operation: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        more(
            collection = collection,
            fiscalYear = fiscalYear,
            typeId = typeId,
            status = status,
            view = view,
            month = month,
            limit = limit,
            offset = offset,
            model = model
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TAX_LIST,
                title = "Taxes",
            )
        )

        loadToast(referer, toast, timestamp, operation, model)
        loadFiscalYears(model, fiscalYear)
        model.addAttribute("collection", getCollection(collection))
        model.addAttribute("view", getView(view))
        model.addAttribute("month", getMonth(month))

        model.addAttribute("statuses", TaxStatus.entries)
        model.addAttribute("status", status)

        model.addAttribute(
            "types",
            typeService.types(objectType = ObjectType.TAX, active = true, limit = Integer.MAX_VALUE)
        )
        model.addAttribute("typeId", typeId)

        return "taxes/list"
    }

    @GetMapping("/taxes/more")
    fun more(
        @RequestParam(required = false, name = "col") collection: String? = null,
        @RequestParam(required = false, name = "fiscal-year") fiscalYear: Int? = null,
        @RequestParam(required = false, name = "type-id") typeId: Long? = null,
        @RequestParam(required = false) status: TaxStatus? = null,
        @RequestParam(required = false, name = "view") view: String? = null,
        @RequestParam(required = false, name = "month") month: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val col = getCollection(collection)
        val userId = currentUser.id()
        val year = getFiscalYear(fiscalYear)
        val xview = getView(view)
        val startAtFrom = LocalDate.parse(getMonth(month) + "-01")

        val taxes = service.taxes(
            participantIds = if (col == COL_MY_REPORTS) {
                userId?.let { id -> listOf(id) } ?: emptyList()
            } else {
                emptyList()
            },

            assigneeIds = if (col == COL_MY_ASSIGNED_REPORTS) {
                userId?.let { id -> listOf(id) } ?: emptyList()
            } else {
                emptyList()
            },

            taxTypeIds = typeId?.let { listOf(typeId) } ?: emptyList(),

            statuses = status?.let { listOf(status) } ?: emptyList(),
            fiscalYear = year,
            startAtFrom = if (xview == VIEW_CALENDAR) startAtFrom else null,
            startAtTo = if (xview == VIEW_CALENDAR) startAtFrom.plusMonths(1).minusDays(1) else null,
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
                if (fiscalYear != null) {
                    url = "$url&fiscal-year=$fiscalYear"
                }
                if (typeId != null) {
                    url = "$url&type-id=$typeId"
                }
                if (status != null) {
                    url = "$url&status=$status"
                }
                if (view != null) {
                    url = "$url&view=$view"
                }
                if (month != null) {
                    url = "$url&month=$month"
                }
                model.addAttribute("moreUrl", url)
            }
        }

        return "taxes/more"
    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        operation: String?,
        model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/taxes/$toast", "/taxes/create"))) {
            if (operation == "del") {
                model.addAttribute("toast", "Deleted")
            } else {
                try {
                    val tax = service.tax(toast, fullGraph = false)
                    model.addAttribute(
                        "toast",
                        "<a href='/taxes/${tax.id}'>${tax.name}</a> has been saved!"
                    )
                } catch (ex: Exception) { // I
                    LOGGER.warn("Unable to load toast information for Account#$toast", ex)
                }
            }
        }
    }

    private fun getCollection(collection: String?): String {
        return when (collection) {
            COL_ALL_REPORTS -> COL_ALL_REPORTS
            COL_MY_REPORTS -> COL_MY_REPORTS
            COL_MY_ASSIGNED_REPORTS -> COL_MY_ASSIGNED_REPORTS
            else -> COL_ALL_REPORTS
        }
    }

    private fun getFiscalYear(fiscalYear: Int? = null): Int {
        return fiscalYear ?: (LocalDate.now().year - 1)
    }

    private fun getView(view: String?): String {
        return when (view) {
            VIEW_CALENDAR -> VIEW_CALENDAR
            else -> VIEW_TABLE
        }
    }

    private fun getMonth(month: String?): String {
        val now = try {
            if (month != null) {
                LocalDate.parse("$month-01")
            } else {
                LocalDate.now()
            }
        } catch (ex: Exception) {
            LocalDate.now()
        }
        return "${now.year}-" +
            if (now.monthValue < 10) "0${now.monthValue}" else now.monthValue.toString()
    }
}
