package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["contact"])
class ContactController(
    private val service: ContactService
) : AbstractContactDetailsController() {
    @GetMapping("/contacts/{id}")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @PathVariable id: Long,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        val contact = service.contact(id)
        if (toast == id && canShowToasts(timestamp, referer, listOf("/contacts/$id/edit", "/contacts/create"))) {
            model.addAttribute("toast", "Saved")
        }
        return show(contact, model)
    }

    private fun show(contact: ContactModel, model: Model): String {
        model.addAttribute("contact", contact)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.CONTACT,
                title = "Contacts",
            )
        )
        return "contacts/show"
    }

    @GetMapping("/contacts/{id}/delete")
    @RequiresPermission(["contact:delete"])
    fun delete(@PathVariable id: Long, model: Model): String {
        val contact = service.contact(id)
        try {
            service.delete(id)
            return "redirect:/contacts?_op=del&_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(contact, model)
        }
    }
}
