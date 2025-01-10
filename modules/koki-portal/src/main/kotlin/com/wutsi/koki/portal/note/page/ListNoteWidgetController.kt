package com.wutsi.koki.portal.note.page

import com.wutsi.koki.portal.note.service.NoteService
import com.wutsi.koki.portal.page.AbstractPageController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ListNoteWidgetController(private val service: NoteService) : AbstractPageController() {
    @GetMapping("/notes/widgets/list")
    fun list(
        @RequestParam(required = false, name = "owner-id") ownerId: Long = -1,
        @RequestParam(required = false, name = "owner-type") ownerType: String = "",
        @RequestParam(name = "test-mode", required = false) testMode: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("testMode", testMode)
        more(ownerId, ownerType, limit, offset, model)
        return "notes/widgets/list"
    }

    @GetMapping("/notes/widgets/list/more")
    fun more(
        @RequestParam(required = false, name = "owner-id") ownerId: Long = -1,
        @RequestParam(required = false, name = "owner-type") ownerType: String = "",
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
        if (notes.size >= limit) {
            val nextOffset = offset + limit
            val url = listOf(
                "/notes/widgets/list/more?limit=$limit&offset=$nextOffset",
                ownerId?.let { "owner-id=$ownerId" },
                ownerType?.let { "owner-id=$ownerType" },
            ).filterNotNull()
                .joinToString(separator = "&")
            model.addAttribute("moreUrl", url)
        }
        return "notes/widgets/list-more"
    }
}
