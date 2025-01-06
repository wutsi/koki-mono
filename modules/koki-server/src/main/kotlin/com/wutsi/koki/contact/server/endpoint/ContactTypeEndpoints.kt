package com.wutsi.koki.contact.server.endpoint

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.contact.dto.GetContactTypeResponse
import com.wutsi.koki.contact.dto.SearchContactTypeResponse
import com.wutsi.koki.contact.server.io.ContactTypeCSVImporter
import com.wutsi.koki.contact.server.mapper.ContactTypeMapper
import com.wutsi.koki.contact.server.service.ContactTypeService
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
@RequestMapping("/v1/contact-types")
class ContactTypeEndpoints(
    private val service: ContactTypeService,
    private val mapper: ContactTypeMapper,
    private val importer: ContactTypeCSVImporter,
) {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetContactTypeResponse {
        val contactType = service.get(id, tenantId)
        return GetContactTypeResponse(mapper.toContactType(contactType))
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "name") names: List<String> = emptyList(),
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchContactTypeResponse {
        val contactTypes = service.search(
            tenantId = tenantId,
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset
        )
        return SearchContactTypeResponse(
            contactTypes = contactTypes.map { contactType -> mapper.toContactTypeSummary(contactType) }
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
