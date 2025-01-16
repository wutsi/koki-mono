package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.page.AbstractPageController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ContactSelectorController(private val service: ContactService) : AbstractPageController() {
    @GetMapping("/contacts/selector/search")
    fun search(
        @RequestParam(required = false, name = "q") keyword: String? = null,
    ): List<ContactModel> {
        return service.contacts(
            keyword = keyword,
            fullGraph = false,
        )
    }
}
