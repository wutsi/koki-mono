package com.wutsi.koki.portal.email.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.email.service.EmailService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ListEmailWidgetController(
    private val service: EmailService,
) {
    @GetMapping("/emails/widgets/list")
    fun list(
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(name = "recipient-id", required = true) recipientId: Long? = null,
        @RequestParam(name = "recipient-type", required = true) recipientType: ObjectType? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("recipientId", recipientId)
        model.addAttribute("recipientType", recipientType)
        model.addAttribute("testMode", testMode)
        more(ownerId, ownerType, limit, offset, model)
        return "emails/widgets/list"
    }

    @GetMapping("/emails/widgets/list/more")
    fun more(
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
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
            val url = listOf(
                "/emails/widgets/list/more?limit=$limit&offset=$nextOffset",
                ownerId?.let { "owner-id=$ownerId" },
                ownerType?.let { "owner-id=$ownerType" },
            ).filterNotNull()
                .joinToString(separator = "&")
            model.addAttribute("moreUrl", url)
        }
        return "emails/widgets/list-more"
    }
}
