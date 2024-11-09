package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.form.dto.ImportFormRequest
import com.wutsi.koki.form.dto.ImportFormResponse
import com.wutsi.koki.form.server.domain.FormEntity
import com.wutsi.koki.form.server.io.FormImporter
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
class ImportFormEndpoint(
    private val importer: FormImporter,
    private val service: FormService,
    private val tenantService: TenantService,
) {
    @PostMapping("/v1/forms")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody @Valid request: ImportFormRequest
    ): ImportFormResponse {
        val form = FormEntity(
            id = UUID.randomUUID().toString(),
            tenant = tenantService.get(tenantId)
        )
        importer.import(form, request.content)
        return ImportFormResponse(
            formId = form.id ?: ""
        )
    }

    @PostMapping("/v1/forms/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
        @RequestBody @Valid request: ImportFormRequest
    ): ImportFormResponse {
        val workflow = service.get(id, tenantId)
        importer.import(workflow, request.content)
        return ImportFormResponse(
            formId = id,
        )
    }
}
