package com.wutsi.koki.employee.server.endpoint

import com.wutsi.koki.employee.dto.CreateEmployeeRequest
import com.wutsi.koki.employee.dto.CreateEmployeeResponse
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.employee.dto.GetEmployeeResponse
import com.wutsi.koki.employee.dto.SearchEmployeeResponse
import com.wutsi.koki.employee.dto.UpdateEmployeeRequest
import com.wutsi.koki.employee.server.mapper.EmployeeMapper
import com.wutsi.koki.employee.server.service.EmployeeService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/employees")
class EmployeeEndpoints(
    private val service: EmployeeService,
    private val mapper: EmployeeMapper,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateEmployeeRequest,
    ): CreateEmployeeResponse {
        val employee = service.create(request, tenantId)
        return CreateEmployeeResponse(employee.id!!)
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateEmployeeRequest,
    ) {
        service.update(id, request, tenantId)
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetEmployeeResponse {
        val employee = service.get(id, tenantId)
        return GetEmployeeResponse(
            employee = mapper.toEmployee(employee)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "status") statuses: List<EmployeeStatus> = emptyList(),
        @RequestParam(required = false, name = "employee-type-id") employeeTypeIds: List<Long> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchEmployeeResponse {
        val employess = service.search(
            tenantId = tenantId,
            ids = ids,
            statuses = statuses,
            employeeTypeIds = employeeTypeIds,
            limit = limit,
            offset = offset
        )
        return SearchEmployeeResponse(
            employees = employess.map { employee -> mapper.toEmployeeSummary(employee) }
        )
    }
}
