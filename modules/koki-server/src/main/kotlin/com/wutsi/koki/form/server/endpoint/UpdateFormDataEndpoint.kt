package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.SubmitFormDataResponse
import com.wutsi.koki.form.server.service.FormDataService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SubmitFormDataEndpoint(
    private val service: FormDataService,
) {
    @PostMapping("/v1/form-data")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody @Valid request: SubmitFormDataRequest
    ): SubmitFormDataResponse {
        val formData = service.submit(request, tenantId)
        return SubmitFormDataResponse(
            formDataId = formData.id!!
        )
    }
}
