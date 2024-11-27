package com.wutsi.koki.file.server.endpoint

import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.file.server.mapper.FileMapper
import com.wutsi.koki.file.server.service.FileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetFileEndpoint(
    private val service: FileService,
    private val mapper: FileMapper,
) {
    @GetMapping("/v1/files/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ): GetFileResponse {
        val file = service.get(id, tenantId)
        return GetFileResponse(
            file = mapper.toFile(file)
        )
    }
}
