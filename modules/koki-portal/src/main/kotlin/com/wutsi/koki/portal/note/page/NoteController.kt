package com.wutsi.koki.portal.note.page

import com.wutsi.koki.portal.note.service.NoteService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
@RequiresPermission(["note"])
class NoteController(private val service: NoteService) {
    @GetMapping("/notes/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val note = service.note(id)
        model.addAttribute("note", note)
        return "notes/show"
    }

    @GetMapping("/notes/{id}/delete")
    @RequiresPermission(["note:delete"])
    fun delete(@PathVariable id: Long): String {
        service.delete(id)
        return "notes/deleted"
    }
}
