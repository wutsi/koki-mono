package com.wutsi.koki.portal.note.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.note.service.NoteService
import com.wutsi.koki.portal.page.AbstractPageController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class NoteTabController(private val service: NoteService) : AbstractPageController() {
    @GetMapping("/notes/tab")
    fun list(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(name = "test-mode", required = false) testMode: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("testMode", testMode)
        more(ownerId, ownerType, limit, offset, model)
        return "notes/tab"
    }

    @GetMapping("/notes/tab/more")
    fun more(
        @RequestParam(required = false, name = "owner-id") ownerId: Long,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType,
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
            val url = "/notes/tab/more?limit=$limit&offset=$nextOffset&owner-id=$ownerId&owner-id=$ownerType"
            model.addAttribute("moreUrl", url)
        }
        return "notes/tab-more"
    }
}
