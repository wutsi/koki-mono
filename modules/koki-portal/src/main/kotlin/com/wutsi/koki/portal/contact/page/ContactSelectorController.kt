package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiresPermission(["contact"])
class ContactSelectorController(private val service: ContactService) : AbstractPageController() {
    @GetMapping("/contacts/selector/search")
    fun search(
        @RequestParam(required = false, name = "q") keyword: String? = null,
    ): List<ContactModel> {
        val user = userHolder.get()
        return service.contacts(
            keyword = keyword,
            accountManagerIds = if (user?.hasFullAccess("contact") == true) emptyList() else listOf(user?.id ?: -1),
            fullGraph = false,
        )
    }
}
