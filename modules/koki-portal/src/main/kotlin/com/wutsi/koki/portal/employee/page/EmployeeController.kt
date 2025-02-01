package com.wutsi.koki.portal.employee.page

import com.wutsi.koki.portal.employee.service.EmployeeService
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["employee"])
class EmployeeController(
    private val service: EmployeeService,
) : AbstractEmployeeDetailsController() {
    @GetMapping("/employees/{id}")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @PathVariable(required = false) id: Long,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        val employee = service.employee(id)
        model.addAttribute("employee", employee)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.EMPLOYEE,
                title = employee.name,
            )
        )

        if (toast == id && canShowToasts(timestamp, referer, listOf("/contacts/$id/edit"))) {
            model.addAttribute("toast", "Saved")
        }
        return "employees/show"
    }
}
