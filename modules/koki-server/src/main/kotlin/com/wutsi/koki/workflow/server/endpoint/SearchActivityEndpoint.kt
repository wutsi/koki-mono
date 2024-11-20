package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.SearchActivityResponse
import com.wutsi.koki.workflow.server.mapper.ActivityMapper
import com.wutsi.koki.workflow.server.service.ActivityService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchActivityEndpoint(
    private val service: ActivityService,
    private val mapper: ActivityMapper,
) {
    @GetMapping("/v1/activities")
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "workflow-id") workflowIds: List<Long> = emptyList(),
        @RequestParam(required = false) type: ActivityType? = null,
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchActivityResponse {
        val activities = service.search(
            ids = ids,
            workflowIds = workflowIds,
            tenantId = tenantId,
            type = type,
            active = active,
            limit = limit,
            offset = offset,
        )
        return SearchActivityResponse(
            activities = activities.map { activity -> mapper.toActivitySummary(activity) }
        )
    }
}
