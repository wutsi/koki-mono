package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.portal.lead.model.LeadModel
import com.wutsi.koki.portal.lead.service.LeadService
import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException

abstract class AbstractLeadDetailsController : AbstractModuleDetailsPageController() {
    @Autowired
    protected lateinit var service: LeadService

    override fun getModuleName(): String {
        return AbstractLeadController.MODULE_NAME
    }

    protected fun findLead(id: Long): LeadModel {
        val lead = service.get(id)

        // Check access
        val user = userHolder.get()
        if (user?.id == lead.listing?.sellerAgentUser?.id || user?.hasFullAccess("lead") == true) {
            return lead
        }

        throw HttpClientErrorException(HttpStatus.FORBIDDEN)
    }
}
