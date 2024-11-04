package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.workflow.dto.ActivityType
import org.springframework.stereotype.Service

@Service
class ActivityExecutorProvider(
    private val start: StartExecutor
) {
    fun get(type: ActivityType): ActivityExecutor {
        return when (type) {
            ActivityType.START -> start
            else -> throw IllegalStateException("Not supported: $type")
        }
    }
}
