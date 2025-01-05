package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.account.dto.GetAttributeResponse
import com.wutsi.koki.account.dto.SearchAttributeResponse
import com.wutsi.koki.account.server.io.AttributeCSVExporter
import com.wutsi.koki.account.server.io.AttributeCSVImporter
import com.wutsi.koki.account.server.service.AttributeService
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.tenant.server.mapper.AttributeMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
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
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/v1/attributes")
class AttributeEndpoints(
    private val importer: AttributeCSVImporter,
    private val exporter: AttributeCSVExporter,
    private val mapper: AttributeMapper,
    private val service: AttributeService,
) {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long
    ): GetAttributeResponse {
        val attribute = service.get(id, tenantId)
        return GetAttributeResponse(
            attribute = mapper.toAttribute(attribute)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "name") names: List<String> = emptyList(),
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchAttributeResponse {
        val attributes = service.search(
            tenantId = tenantId,
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset
        )
        return SearchAttributeResponse(
            attributes = attributes.map { attribute -> mapper.toAttributeSummary(attribute) }
        )
    }

    @PostMapping("/csv", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun import(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestPart file: MultipartFile
    ): ImportResponse {
        return importer.import(file.inputStream, tenantId)
    }

    @GetMapping("/csv")
    fun export(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        response: HttpServletResponse,
    ) {
        response.setHeader("Content-Type", "text/csv")
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment()
                .filename("tenant_$tenantId-attributes.csv", StandardCharsets.UTF_8)
                .build()
                .toString()
        )
        exporter.export(response.outputStream, tenantId)
    }
}
