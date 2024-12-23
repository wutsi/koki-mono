package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.form.dto.GetFormDataResponse
import com.wutsi.koki.form.server.mapper.FormDataMapper
import com.wutsi.koki.form.server.service.FormDataService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetFormDataEndpoint(
    private val service: FormDataService,
    private val mapper: FormDataMapper,
) {
    @GetMapping("/v1/form-data/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String
    ): GetFormDataResponse {
        val formData = service.get(id, tenantId)
        return GetFormDataResponse(
            formData = mapper.toFormData(formData)
        )
    }
}
