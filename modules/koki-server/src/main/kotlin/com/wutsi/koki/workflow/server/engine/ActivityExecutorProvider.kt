package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.server.service.StartExecutor
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

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
