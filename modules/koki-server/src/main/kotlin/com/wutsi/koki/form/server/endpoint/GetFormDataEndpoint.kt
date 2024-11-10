package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.form.dto.SaveFormDataRequest
import com.wutsi.koki.form.dto.SaveFormDataResponse
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.form.server.service.FormService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SaveFormDataEndpoint(
    private val service: FormDataService,
    private val formService: FormService,
) {
    @PostMapping("/v1/forms/{id}/data")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @RequestBody @Valid request: SaveFormDataRequest
    ): SaveFormDataResponse {
        val form = formService.get(id, tenantId)
        val formData = service.save(request.data, form)
        return SaveFormDataResponse(
            formDataId = formData.id ?: ""
        )
    }

    @PostMapping("/v1/forms/{id}/data/{data-id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @PathVariable(name = "data-id") formDataId: String,
        @RequestBody @Valid request: SaveFormDataRequest
    ): SaveFormDataResponse {
        val form = formService.get(id, tenantId)
        val formData = service.get(formDataId, form)
        service.save(request.data, formData)
        return SaveFormDataResponse(
            formDataId = formDataId
        )
    }
}
