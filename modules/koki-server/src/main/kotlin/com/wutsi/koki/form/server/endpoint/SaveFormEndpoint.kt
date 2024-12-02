package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.form.dto.SaveFormRequest
import com.wutsi.koki.form.dto.SaveFormResponse
import com.wutsi.koki.form.server.domain.FormEntity
import com.wutsi.koki.form.server.service.FormService
import com.wutsi.koki.tenant.server.service.TenantService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping
class SaveFormEndpoint(
    private val service: FormService,
    private val tenantService: TenantService,
) {
    @PostMapping("/v1/forms")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody @Valid request: SaveFormRequest
    ): SaveFormResponse {
        val form = FormEntity(
            id = UUID.randomUUID().toString(),
            tenantId = tenantService.get(tenantId).id!!
        )
        service.save(form, request)
        return SaveFormResponse(
            formId = form.id ?: ""
        )
    }

    @PostMapping("/v1/forms/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @RequestBody @Valid request: SaveFormRequest
    ): SaveFormResponse {
        val form = service.get(id, tenantId)
        service.save(form, request)
        return SaveFormResponse(
            formId = id,
        )
    }
}
