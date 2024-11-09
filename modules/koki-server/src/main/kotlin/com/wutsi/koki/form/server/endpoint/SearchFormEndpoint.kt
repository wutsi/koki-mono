package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.form.server.mapper.FormMapper
import com.wutsi.koki.form.server.service.FormService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetFormEndpoint(
    private val service: FormService,
    private val mapper: FormMapper,
) {
    @GetMapping("/v1/forms/{id}")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ): GetFormResponse {
        val form = service.get(id, tenantId)
        return GetFormResponse(
            form = mapper.toForm(form)
        )
    }
}
