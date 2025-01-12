package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.tenant.server.io.RoleCSVImporter
import com.wutsi.koki.tenant.server.mapper.RoleMapper
import com.wutsi.koki.tenant.server.service.RoleService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/v1/roles")
class RoleEndpoints(
    private val importer: RoleCSVImporter,
    private val service: RoleService,
    private val mapper: RoleMapper,
) {
    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false) id: List<Long> = emptyList(),
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) name: List<String> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchRoleResponse {
        return SearchRoleResponse(
            roles = service.search(
                ids = id,
                names = name,
                active = active,
                tenantId = tenantId,
                limit = limit,
                offset = offset,
            ).map { attr -> mapper.toRole(attr) }
        )
    }

    @PostMapping("/csv", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun import(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestPart file: MultipartFile
    ): ImportResponse =
        importer.import(tenantId, file.inputStream)
}
