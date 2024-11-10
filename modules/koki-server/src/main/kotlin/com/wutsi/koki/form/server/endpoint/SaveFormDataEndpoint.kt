package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.form.dto.CreateFormDataRequest
import com.wutsi.koki.form.dto.CreateFormDataResponse
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.form.server.service.FormService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CreateFormDataEndpoint(
    private val service: FormDataService,
    private val formService: FormService,
) {
    @PostMapping("/v1/form-data")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody @Valid request: CreateFormDataRequest
    ): CreateFormDataResponse {
        val form = formService.get(request.formId, tenantId)
        val formData = service.save(request.data, form)
        return CreateFormDataResponse(
            formDataId = formData.id ?: ""
        )
    }
}
