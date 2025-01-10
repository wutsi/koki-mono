package com.wutsi.koki.portal.note.page

import com.wutsi.koki.portal.note.service.NoteService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class NoteController(private val service: NoteService) {
    @GetMapping("/notes/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        service.delete(id)
        return "notes/deleted"
    }
}
