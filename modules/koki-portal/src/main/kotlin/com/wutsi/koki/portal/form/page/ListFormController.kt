package com.wutsi.koki.portal.form.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.form.service.FormService
import com.wutsi.koki.portal.security.RequiresPermission
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/forms")
@RequiresPermission(["form"])
class ListFormController(
    private val service: FormService
) : AbstractFormController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ListFormController::class.java)
    }

    @GetMapping
    fun list(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model,
    ): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FORM_LIST,
                title = "Forms",
            )
        )

        model.addAttribute("active", active)
        loadToast(referer, toast, timestamp, model)
        more(active, limit, offset, model)
        return "forms/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val forms = service.forms(
            active = active,
            limit = limit,
            offset = offset
        )
        if (forms.isNotEmpty()) {
            model.addAttribute("forms", forms)
            if (forms.size >= limit) {
                val nextOffset = offset + limit
                var url = "/forms/more?limit=$limit&offset=$nextOffset"
                if (active != null) {
                    url = "$url&active=$active"
                }
                model.addAttribute("moreUrl", url)
            }
        }

        return "forms/more"
    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/forms/$toast", "/forms/create"))) {
            try {
                val form = service.form(toast)
                model.addAttribute(
                    "toast",
                    "<a href='/forms/${form.id}'>${form.name}</a> has been saved!"
                )
            } catch (ex: Exception) { // I
                LOGGER.warn("Unable to load toast information for Form#$toast", ex)
            }
        }
    }
}
