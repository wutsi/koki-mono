package com.wutsi.koki.portal.room.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractRoomController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "room"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
