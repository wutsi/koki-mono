package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.service.TaxService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["tax"])
class TaxTabController(private val service: TaxService) : AbstractPageController() {
    @GetMapping("/taxes/tab")
    fun list(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        if (ownerType == ObjectType.ACCOUNT) {
            model.addAttribute("addUrl", "/taxes/create?account-id=$ownerId")
        }
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("ownerId", ownerId)
        more(
            ownerId = ownerId,
            ownerType = ownerType,
            limit = limit,
            offset = offset,
            model = model
        )
        return "taxes/tab"
    }

    @GetMapping("/taxes/tab/more")
    fun more(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val accountId = if (ownerType == ObjectType.ACCOUNT) {
            ownerId
        } else {
            null
        }

        if (accountId != null) {
            val taxes = service.taxes(
                accountIds = listOf(ownerId),
                limit = limit,
                offset = offset,
            )

            model.addAttribute("taxes", taxes)
            model.addAttribute("showAccount", false)
            if (taxes.size >= limit) {
                val nextOffset = offset + limit
                val url =
                    "/taxes/tab/more?limit=$limit&offset=$nextOffset&owner-id=$ownerId&owner-type=$ownerType"
                model.addAttribute("moreUrl", url)
            }
        }
        return "taxes/more"
    }
}
