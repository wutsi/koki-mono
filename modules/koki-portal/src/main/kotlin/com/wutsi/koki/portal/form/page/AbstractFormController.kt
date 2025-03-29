package com.wutsi.koki.portal.form.page

import com.vladmihalcea.hibernate.util.LogUtils.LOGGER
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.portal.form.service.FormService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/forms")
class ListFormController(
    private val service: FormService
) : Abstract{
    @GetMapping
    fun list(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model,
    ){

    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/employees/$toast", "/employees/create"))) {
            try {
                val employee = service.employee(toast)
                model.addAttribute(
                    "toast",
                    "<a href='/employees/${employee.user.id}'>${employee.user.displayName}</a> has been saved!"
                )
            } catch (ex: Exception) { // I
                LOGGER.warn("Unable to load toast information for Employee#$toast", ex)
            }
        }
    }
}
