package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.listing.service.ListingService
import com.wutsi.koki.portal.module.page.AbstractModulePageController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.mvc.AbstractController

abstract class AbstractLeadController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "lead"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
