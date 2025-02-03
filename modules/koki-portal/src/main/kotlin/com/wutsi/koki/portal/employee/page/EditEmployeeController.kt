package com.wutsi.koki.portal.employee.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.portal.employee.form.UpdateEmployeeForm
import com.wutsi.koki.portal.employee.model.EmployeeModel
import com.wutsi.koki.portal.employee.service.EmployeeService
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.TypeService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException
import java.text.SimpleDateFormat
import java.util.Currency

@Controller
@RequiresPermission(["employee:manage"])
class EditEmployeeController(
    private val service: EmployeeService,
    private val typeService: TypeService,
) : AbstractEmployeeController() {
    @GetMapping("/employees/{id}/edit")
    fun create(@PathVariable id: Long, model: Model): String {
        val employee = service.employee(id)
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val form = UpdateEmployeeForm(
            jobTitle = employee.jobTitle,
            status = employee.status,
            hiredAt = employee.hiredAt?.let { date -> fmt.format(date) },
            terminatedAt = employee.terminatedAt?.let { date -> fmt.format(date) },
            hourlyWage = employee.hourlyWage?.value,
            currency = employee.hourlyWage?.currency,
            employeeTypeId = employee.employeeType?.id,
        )
        return edit(employee, form, model)
    }

    fun edit(employee: EmployeeModel, form: UpdateEmployeeForm, model: Model): String {
        model.addAttribute("employee", employee)
        model.addAttribute("form", form)
        model.addAttribute("statuses", EmployeeStatus.entries.filter { entry -> entry != EmployeeStatus.UNKNOWN })

        val currency = Currency.getInstance(tenantHolder.get()?.currency)
        model.addAttribute("currencies", listOf(currency))

        model.addAttribute(
            "types",
            typeService.types(objectType = ObjectType.EMPLOYEE, limit = Integer.MAX_VALUE)
                .filter { type -> type.id == employee.employeeType?.id || type.active }
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.EMPLOYEE_EDIT,
                title = employee.name,
            )
        )

        return "employees/edit"
    }

    @PostMapping("/employees/{id}/update")
    fun addNew(
        @PathVariable id: Long,
        @ModelAttribute form: UpdateEmployeeForm,
        model: Model
    ): String {
        try {
            service.update(id, form)
            return "redirect:/employees?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val employee = service.employee(id)
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(employee, form, model)
        }
    }
}
