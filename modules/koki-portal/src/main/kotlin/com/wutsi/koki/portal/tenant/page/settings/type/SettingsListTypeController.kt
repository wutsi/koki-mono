package com.wutsi.koki.portal.tenant.page.settings.type

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.TypeService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["tenant:admin"])
class SettingsListTypeController(private val service: TypeService) : AbstractSettingsTypeController() {
    @GetMapping("/settings/tenant/types")
    fun list(
        @RequestParam(required = false, name = "object-type") objectType: ObjectType? = null,
        @RequestParam(required = false) active: Boolean? = true,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TENANT_SETTINGS_TYPE_LIST,
                title = "Types",
            )
        )

        model.addAttribute("objectType", objectType)
        model.addAttribute("objectTypes", getObjectTypes())

        model.addAttribute("active", active)

        more(objectType, active, limit, offset, model)
        return "tenant/settings/types/list"
    }

    @GetMapping("/settings/tenant/types/more")
    fun more(
        @RequestParam(required = false, name = "object-type") objectType: ObjectType? = null,
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val types = service.types(
            objectType = objectType,
            active = active,
            limit = limit,
            offset = offset
        )

        model.addAttribute("types", types)
        if (types.size >= limit) {
            val nextOffset = offset + limit
            var moreUrl = "/settings/tenant/types/more?limit=$limit&offset=$nextOffset"
            if (objectType != null) {
                moreUrl = "$moreUrl&object-type=$objectType"
            }
            if (active != null) {
                moreUrl = "$moreUrl&active=$active"
            }
            model.addAttribute("moreUrl", moreUrl)
        }
        return "tenant/settings/types/more"
    }
}
