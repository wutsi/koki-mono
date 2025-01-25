package com.wutsi.koki.portal.note.page

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.note.form.NoteForm
import com.wutsi.koki.portal.note.service.NoteService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class EditNoteController(private val service: NoteService) {
    @GetMapping("/notes/{id}/edit")
    fun edit(@PathVariable id: Long, model: Model): String {
        val note = service.note(id)
        model.addAttribute("note", note)
        model.addAttribute("hours", 0..23)
        model.addAttribute("minutes", 0..60)
        model.addAttribute(
            "form",
            NoteForm(
                subject = note.subject,
                body = note.body,
                type = note.type,
                durationMinutes = note.durationMinutes,
                durationHours = note.durationHours,
            )
        )
        model.addAttribute(
            "page",
            PageModel()
        )
        return "notes/edit"
    }

    @PostMapping("/notes/{id}/update")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: NoteForm,
        model: Model
    ): String {
        service.update(id, form)
        return "notes/saved"
    }
}
