package com.wutsi.koki.portal.note.page

import com.wutsi.koki.portal.note.service.NoteService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class NoteFragmentController(private val service: NoteService) {
    @GetMapping("/notes/{id}/fragment")
    fun fragment(@PathVariable id: Long, model: Model): String {
        val note = service.note(id)
        model.addAttribute("note", note)
        return "notes/fragment"
    }
}
