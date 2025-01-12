package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.tax.dto.GetTaxTypeResponse
import com.wutsi.koki.tax.dto.SearchTaxTypeResponse
import com.wutsi.koki.tax.server.io.TaxTypeCSVImporter
import com.wutsi.koki.tax.server.mapper.TaxTypeMapper
import com.wutsi.koki.tax.server.service.TaxTypeService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/v1/tax-types")
class TaxTypeEndpoints(
    private val service: TaxTypeService,
    private val importer: TaxTypeCSVImporter,
    private val mapper: TaxTypeMapper,
) {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetTaxTypeResponse {
        val taxType = service.get(id, tenantId)
        return GetTaxTypeResponse(mapper.toTaxType(taxType))
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "name") names: List<String> = emptyList(),
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchTaxTypeResponse {
        val taxTypes = service.search(
            tenantId = tenantId,
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset
        )
        return SearchTaxTypeResponse(
            taxTypes = taxTypes.map { taxType -> mapper.toTaxTypeSummary(taxType) }
        )
    }

    @PostMapping("/csv", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun import(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestPart file: MultipartFile
    ): ImportResponse {
        return importer.import(file.inputStream, tenantId)
    }
}
