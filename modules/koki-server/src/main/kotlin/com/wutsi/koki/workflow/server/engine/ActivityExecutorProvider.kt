package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.server.service.ManualExecutor
import com.wutsi.koki.workflow.server.service.StartExecutor
import com.wutsi.koki.workflow.server.service.StopExecutor
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

@Service
class ActivityExecutorProvider(
    private val start: StartExecutor,
    private val stop: StopExecutor,
    private val manual: ManualExecutor,
) {
    fun get(type: ActivityType): ActivityExecutor {
        return when (type) {
            ActivityType.START -> start
            ActivityType.STOP -> stop
            ActivityType.MANUAL -> manual
            else -> throw IllegalStateException("Not supported: $type")
        }
    }
}
