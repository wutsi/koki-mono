package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.form.server.service.FormDataService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class UpdateFormDataEndpoint(
    private val service: FormDataService,
) {
    @PostMapping("/v1/form-data/{id}")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @RequestBody @Valid request: UpdateFormDataRequest
    ) {
        service.update(id, request, tenantId)
    }
}
