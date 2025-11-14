package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.portal.common.model.MoneyModel
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.lead.model.LeadModel
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.security.RequiresPermission
import io.micrometer.core.instrument.Metrics.more
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Date

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
