package com.wutsi.koki.portal.note.page

import com.wutsi.koki.portal.note.form.NoteForm
import com.wutsi.koki.portal.note.service.NoteService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class ListNoteWidgetController(private val service: NoteService) {
    @GetMapping("/notes/widgets/list")
    fun list(
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        more(ownerId, ownerType, limit, offset, model)
        return "notes/widgets/list"
    }

    @GetMapping("/notes/widgets/list/more")
    fun more(
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: String? = null,
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

    @ResponseBody
    @GetMapping("/notes/{id}/delete")
    fun delete(@PathVariable id: Long): Map<String, Any> {
        service.delete(id)
        return emptyMap()
    }

    @PostMapping("/notes/add-new")
    fun addNew(@ModelAttribute form: NoteForm, model: Model): String {
        service.create(form)
        return "notes/widgets/saved"
    }
}
