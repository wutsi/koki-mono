package com.wutsi.koki.file.server.endpoint

import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.file.server.mapper.FileMapper
import com.wutsi.koki.file.server.service.FileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchFileEndpoint(
    private val service: FileService,
    private val mapper: FileMapper,
) {
    @GetMapping("/v1/files")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceIds: List<String> = emptyList(),
        @RequestParam(required = false, name = "form-id") formIds: List<String> = emptyList(),
        @RequestParam(required = false) limit: Int = 200,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchFileResponse {
        val files = service.search(
            tenantId = tenantId,
            ids = ids,
            workflowInstanceIds = workflowInstanceIds,
            formIds = formIds,
            limit = limit,
            offset = offset
        )
        return SearchFileResponse(
            files = files.map { file -> mapper.toFileSummary(file) }
        )
    }
}
