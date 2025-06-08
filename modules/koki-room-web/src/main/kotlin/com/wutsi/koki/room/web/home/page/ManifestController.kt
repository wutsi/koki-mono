package com.wutsi.koki.room.web.home.page

import com.wutsi.koki.room.web.common.page.AbstractPageController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class ManifestController : AbstractPageController() {
    @ResponseBody
    @GetMapping("/manifest.json")
    fun show(): Map<String, Any> {
        val tenant = tenantHolder.get()!!
        return mapOf(
            "name" to tenant.name,
            "shortName" to tenant.name,
            "start_url" to tenant.clientPortalUrl,
            "display" to "fullscreen",
            "scope" to "/",
            "orientation" to "any",
            "icons" to listOf(
                mapOf(
                    "src" to tenant.iconUrl,
                    "sizes" to "192x192"
                )
            )
        )
    }
}
