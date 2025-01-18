package com.wutsi.koki.module.server.endpoint

import com.wutsi.koki.module.dto.SearchPermissionResponse
import com.wutsi.koki.module.server.mapper.PermissionMapper
import com.wutsi.koki.module.server.service.PermissionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/permissions")
class PermissionEndpoints(
    private val service: PermissionService,
    private val mapper: PermissionMapper,
) {
    @GetMapping
    fun search(
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "module-id") moduleIds: List<Long> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchPermissionResponse {
        val permissions = service.search(
            ids = ids,
            moduleIds = moduleIds,
            limit = limit,
            offset = offset
        )
        return SearchPermissionResponse(
            permissions = permissions.map { permission -> mapper.toPermission(permission) }
        )
    }
}
