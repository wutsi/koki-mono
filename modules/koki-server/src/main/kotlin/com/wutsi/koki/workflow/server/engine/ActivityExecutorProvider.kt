package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.server.service.ManualExecutor
import com.wutsi.koki.workflow.server.service.StartExecutor
import com.wutsi.koki.workflow.server.service.StopExecutor
import com.wutsi.koki.workflow.server.service.UserExecutor
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

@Service
class ActivityExecutorProvider(
    private val start: StartExecutor,
    private val stop: StopExecutor,
    private val manual: ManualExecutor,
    private val user: UserExecutor
) {
    fun get(type: ActivityType): ActivityExecutor {
        return when (type) {
            ActivityType.START -> start
            ActivityType.STOP -> stop
            ActivityType.MANUAL -> manual
            ActivityType.USER -> user
            else -> throw IllegalStateException("Not supported: $type")
        }
    }
}
