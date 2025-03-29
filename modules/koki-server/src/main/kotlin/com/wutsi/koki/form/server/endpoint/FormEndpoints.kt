package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.form.dto.CreateFormRequest
import com.wutsi.koki.form.dto.CreateFormResponse
import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.form.dto.UpdateFormRequest
import com.wutsi.koki.form.server.mapper.FormMapper
import com.wutsi.koki.form.server.service.FormService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/forms")
class FormEndpoints(
    private val service: FormService,
    private val mapper: FormMapper,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateFormRequest,
    ): CreateFormResponse {
        val form = service.create(request, tenantId)
        return CreateFormResponse(form.id!!)
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateFormRequest,
    ) {
        service.update(id, request, tenantId)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        service.delete(id, tenantId)
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetFormResponse {
        val form = service.get(id, tenantId)
        return GetFormResponse(
            form = mapper.toForm(form)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchFormResponse {
        val forms = service.search(
            tenantId = tenantId,
            ids = ids,
            active = active,
            ownerId = ownerId,
            ownerType = ownerType,
            limit = limit,
            offset = offset
        )
        return SearchFormResponse(
            forms = forms.map { form -> mapper.toFormSummary(form) }
        )
    }
}
