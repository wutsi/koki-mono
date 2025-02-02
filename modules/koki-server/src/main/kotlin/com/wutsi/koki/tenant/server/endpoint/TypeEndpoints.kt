package com.wutsi.koki.contact.server.endpoint

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.tenant.dto.GetTypeResponse
import com.wutsi.koki.tenant.dto.SearchTypeResponse
import com.wutsi.koki.tenant.server.io.TypeCSVImporter
import com.wutsi.koki.tenant.server.mapper.TypeMapper
import com.wutsi.koki.tenant.server.service.TypeService
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
@RequestMapping("/v1/types")
class TypeEndpoints(
    private val service: TypeService,
    private val mapper: TypeMapper,
    private val importer: TypeCSVImporter,
) {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetTypeResponse {
        val contactType = service.get(id, tenantId)
        return GetTypeResponse(mapper.toType(contactType))
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false, name = "object-type") objectType: ObjectType? = null,
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchTypeResponse {
        val types = service.search(
            tenantId = tenantId,
            ids = ids,
            keyword = keyword,
            objectType = objectType,
            active = active,
            limit = limit,
            offset = offset
        )
        return SearchTypeResponse(
            types = types.map { contactType -> mapper.toTypeSummary(contactType) }
        )
    }

    @PostMapping("/csv", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun import(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(name = "object-type") objectType: ObjectType,
        @RequestPart file: MultipartFile
    ): ImportResponse {
        return importer.import(file.inputStream, objectType, tenantId)
    }
}
