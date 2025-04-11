package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.tax.dto.GetTaxFileResponse
import com.wutsi.koki.tax.server.mapper.TaxFileMapper
import com.wutsi.koki.tax.server.service.TaxFileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/tax-files")
class TaxFileEndpoints(
    private val service: TaxFileService,
    private val mapper: TaxFileMapper,
) {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetTaxFileResponse {
        val taxFile = service.get(id, tenantId)
        return GetTaxFileResponse(
            taxFile = mapper.toTaxFile(taxFile)
        )
    }
}
