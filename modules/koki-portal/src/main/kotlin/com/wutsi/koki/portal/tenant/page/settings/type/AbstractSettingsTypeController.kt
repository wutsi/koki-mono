package com.wutsi.koki.portal.tenant.page.settings.type

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.AbstractPageController

abstract class AbstractSettingsTypeController : AbstractPageController() {
    fun getObjectTypes(): List<ObjectType> {
        return listOf(
            ObjectType.ACCOUNT,
            ObjectType.CONTACT,
            ObjectType.EMPLOYEE,
            ObjectType.TAX,
        )
    }
}
