package com.wutsi.koki.portal.employee.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.employee.service.EmployeeService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.TypeService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["employee"])
class ListEmployeeController(
    private val service: EmployeeService,
    private val typeService: TypeService,
) : AbstractEmployeeController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ListEmployeeController::class.java)
    }

    @GetMapping("/employees")
    fun list(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false) status: EmployeeStatus? = null,
        @RequestParam(required = false, name = "type-id") typeId: Long? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        model.addAttribute("status", status)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.EMPLOYEE_LIST,
                title = "Employees",
            )
        )

        model.addAttribute("statuses", EmployeeStatus.entries.filter { entry -> entry != EmployeeStatus.UNKNOWN })
        model.addAttribute("status", status)
        model.addAttribute("typeId", typeId)

        loadToast(referer, toast, timestamp, model)
        more(status, typeId, limit, offset, model)
        return "employees/list"
    }

    @GetMapping("/employees/more")
    fun more(
        @RequestParam(required = false) status: EmployeeStatus? = null,
        @RequestParam(required = false, name = "type-id") typeId: Long? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val employees = service.employees(
            statuses = status?.let { listOf(status) } ?: emptyList(),
            employeeTypeIds = typeId?.let { listOf(typeId) } ?: emptyList(),
            limit = limit,
            offset = offset
        )

        model.addAttribute(
            "types",
            typeService.types(objectType = ObjectType.EMPLOYEE, active = true, limit = Integer.MAX_VALUE)
        )

        if (employees.isNotEmpty()) {
            model.addAttribute("employees", employees)
            if (employees.size >= limit) {
                val nextOffset = offset + limit
                var url = "/employees/more?limit=$limit&offset=$nextOffset"
                if (status != null) {
                    url = "$url&status=$status"
                }
                if (typeId != null) {
                    url = "$url&type-id=$typeId"
                }
                model.addAttribute("moreUrl", url)
            }
        }

        return "employees/more"
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
