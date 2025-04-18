package com.wutsi.koki.portal.note.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.note.form.NoteForm
import com.wutsi.koki.portal.note.service.NoteService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["note:manage"])
class CreateNoteController(private val service: NoteService) {
    @GetMapping("/notes/create")
    fun create(
        @RequestParam(name = "owner-id", required = true) ownerId: Long = -1,
        @RequestParam(name = "owner-type", required = true) ownerType: ObjectType = ObjectType.UNKNOWN,
        model: Model
    ): String {
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("hours", 0..23)
        model.addAttribute("minutes", 0..60)
        model.addAttribute(
            "form",
            NoteForm(
                ownerType = ownerType,
                ownerId = ownerId
            )
        )
        return "notes/create"
    }

    @PostMapping("/notes/add-new")
    fun addNew(@ModelAttribute form: NoteForm, model: Model): String {
        service.create(form)
        return "notes/saved"
    }
}
