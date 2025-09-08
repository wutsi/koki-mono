package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.HttpClientErrorException

abstract class AbstractContactDetailsController : AbstractModuleDetailsPageController() {
    @Autowired
    protected lateinit var contactService: ContactService

    override fun getModuleName(): String {
        return AbstractContactController.MODULE_NAME
    }

    protected fun findContact(id: Long): ContactModel {
        val contact = contactService.get(id)
        if (!contact.canAccess(userHolder.get())) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(403))
        }
        return contact
    }
}
