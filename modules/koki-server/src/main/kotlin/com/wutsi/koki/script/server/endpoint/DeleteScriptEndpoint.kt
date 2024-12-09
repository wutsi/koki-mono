package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.script.server.service.ScriptService
import com.wutsi.koki.workflow.server.service.ActivityService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class DeleteScriptEndpoint(
    private val service: ScriptService,
    private val activityService: ActivityService,
) {
    @DeleteMapping("/v1/scripts/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String,
    ) {
        /** Check if in used */
        val activities = activityService.search(tenantId = tenantId, formIds = listOf(id))
        if (activities.isNotEmpty()) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.SCRIPT_IN_USE,
                    data = mapOf(
                        "activities" to activities.map { activity -> activity.name }
                    )
                )
            )
        }

        /* delete */
        service.delete(id, tenantId)
    }
}
