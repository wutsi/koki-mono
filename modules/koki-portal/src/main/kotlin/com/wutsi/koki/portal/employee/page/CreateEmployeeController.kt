package com.wutsi.koki.portal.employee.page

import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.portal.employee.form.CreateEmployeeForm
import com.wutsi.koki.portal.employee.service.EmployeeService
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException
import java.util.Currency

@Controller
@RequiresPermission(["employee:manage"])
class CreateEmployeeController(
    private val service: EmployeeService,
) : AbstractEmployeeController() {
    @GetMapping("/employees/create")
    fun create(model: Model): String {
        val form = CreateEmployeeForm(
            currency = tenantHolder.get()?.currency,
            status = EmployeeStatus.ACTIVE,
        )
        return create(form, model)
    }

    fun create(form: CreateEmployeeForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute("statuses", EmployeeStatus.entries.filter { entry -> entry != EmployeeStatus.UNKNOWN })

        val currency = Currency.getInstance(tenantHolder.get()?.currency)
        model.addAttribute("currencies", listOf(currency))

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.EMPLOYEE_CREATE,
                title = "New Employees",
            )
        )

        return "employees/create"
    }

    @PostMapping("/employees/add-new")
    fun addNew(
        @ModelAttribute form: CreateEmployeeForm,
        model: Model
    ): String {
        try {
            val employeeId = service.create(form)
            return "redirect:/employees?_toast=$employeeId&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(form, model)
        }
    }
}
