package com.wutsi.koki.portal.room.page

import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController

abstract class AbstractRoomDetailsController : AbstractModuleDetailsPageController() {
    override fun getModuleName(): String {
        return AbstractRoomController.MODULE_NAME
    }
}
