package com.wutsi.koki.portal.note.page

import com.wutsi.koki.portal.note.service.NoteService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ListNoteWidgetController(private val service: NoteService) {
    @GetMapping("/notes/widgets/list")
    fun list(
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: String? = null,
        @RequestParam(required = false, name = "return-url") returnUrl: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        more(ownerId, ownerType, returnUrl, limit, offset, model)
        return "notes/widgets/list"
    }

    @GetMapping("/notes/widgets/list/more")
    fun more(
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: String? = null,
        @RequestParam(required = false, name = "return-url") returnUrl: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val notes = service.notes(
            ownerId = ownerId,
            ownerType = ownerType,
            limit = limit,
            offset = offset,
        )
        model.addAttribute("notes", notes)
        model.addAttribute("returnUrl", returnUrl)
        if (notes.size >= limit) {
            val nextOffset = offset + limit
            val url = listOf(
                "/notes/widgets/list/more?limit=$limit&offset=$nextOffset",
                ownerId?.let { "owner-id=$ownerId" },
                ownerType?.let { "owner-id=$ownerType" },
                returnUrl?.let { "return-url=$returnUrl" },
            ).filterNotNull()
                .joinToString(separator = "&")
            model.addAttribute("moreUrl", url)
        }
        return "files/widgets/list-more"
    }
}
