package com.wutsi.koki.portal.note.page

import com.wutsi.koki.portal.note.form.NoteForm
import com.wutsi.koki.portal.note.service.NoteService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
class CreateNoteController(private val service: NoteService) {
    @PostMapping("/notes/add-new")
    fun addNew(@ModelAttribute form: NoteForm, model: Model): String {
        service.create(form)
        return "notes/saved"
    }
}
