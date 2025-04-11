package com.wutsi.koki.sdk

import com.wutsi.koki.employee.dto.CreateEmployeeRequest
import com.wutsi.koki.employee.dto.CreateEmployeeResponse
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.employee.dto.GetEmployeeResponse
import com.wutsi.koki.employee.dto.SearchEmployeeResponse
import com.wutsi.koki.employee.dto.UpdateEmployeeRequest
import org.springframework.web.client.RestTemplate

class KokiEmployees(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PATH_PREFIX = "/v1/employees"
    }

    fun create(request: CreateEmployeeRequest): CreateEmployeeResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateEmployeeResponse::class.java).body
    }

    fun update(id: Long, request: UpdateEmployeeRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun employee(id: Long): GetEmployeeResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetEmployeeResponse::class.java).body
    }

    fun employees(
        ids: List<Long>,
        statuses: List<EmployeeStatus>,
        employeeTypeIds: List<Long>,
        limit: Int,
        offset: Int,
    ): SearchEmployeeResponse {
        val url = urlBuilder.build(
            PATH_PREFIX, mapOf(
                "id" to ids,
                "status" to statuses,
                "employee-type-id" to employeeTypeIds,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchEmployeeResponse::class.java).body
    }
}
