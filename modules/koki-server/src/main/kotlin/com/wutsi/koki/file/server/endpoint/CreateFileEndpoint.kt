package com.wutsi.koki.document.server.endpoint

import com.wutsi.koki.document.dto.CreateFileRequest
import com.wutsi.koki.document.dto.CreateFileResponse
import com.wutsi.koki.document.server.service.FileService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CreateFileEndpoint(
    private val service: FileService
) {
    @PostMapping("/v1/files")
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody @Valid request: CreateFileRequest
    ): CreateFileResponse {
        val file = service.create(request, tenantId)
        return CreateFileResponse(fileId = file.id!!)
    }
}
