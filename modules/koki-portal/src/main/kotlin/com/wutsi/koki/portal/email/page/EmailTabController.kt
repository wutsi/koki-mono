package com.wutsi.koki.portal.email.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.email.service.EmailService
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["email"])
class EmailTabController(private val service: EmailService) : AbstractPageController() {
    @GetMapping("/emails/tab")
    fun list(
        @RequestParam(required = false, name = "owner-id") ownerId: Long,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("testMode", testMode)
        more(ownerId, ownerType, limit, offset, model)
        return "emails/tab"
    }

    @GetMapping("/emails/tab/more")
    fun more(
        @RequestParam(required = false, name = "owner-id") ownerId: Long,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        val emails = service.emails(
            ownerId = ownerId,
            ownerType = ownerType,
            limit = limit,
            offset = offset,
        )
        model.addAttribute("emails", emails)

        if (emails.size >= limit) {
            val nextOffset = offset + limit
            val url = "/emails/tab/more?limit=$limit&offset=$nextOffset&owner-id=$ownerId&owner-id=$ownerType"
            model.addAttribute("moreUrl", url)
        }
        return "emails/tab-more"
    }
}
