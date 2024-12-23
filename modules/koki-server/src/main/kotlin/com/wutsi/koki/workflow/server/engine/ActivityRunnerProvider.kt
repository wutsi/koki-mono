package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.server.engine.runner.ServiceRunner
import com.wutsi.koki.workflow.server.service.runner.EndRunner
import com.wutsi.koki.workflow.server.service.runner.ManualRunner
import com.wutsi.koki.workflow.server.service.runner.ReceiveRunner
import com.wutsi.koki.workflow.server.service.runner.ScriptRunner
import com.wutsi.koki.workflow.server.service.runner.SendRunner
import com.wutsi.koki.workflow.server.service.runner.StartRunner
import com.wutsi.koki.workflow.server.service.runner.UserRunner
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

@Service
class ActivityRunnerProvider(
    private val start: StartRunner,
    private val end: EndRunner,
    private val manual: ManualRunner,
    private val user: UserRunner,
    private val send: SendRunner,
    private val receive: ReceiveRunner,
    private val script: ScriptRunner,
    private val service: ServiceRunner,
) {
    fun get(type: ActivityType): ActivityRunner {
        return when (type) {
            ActivityType.START -> start
            ActivityType.END -> end
            ActivityType.MANUAL -> manual
            ActivityType.USER -> user
            ActivityType.SEND -> send
            ActivityType.RECEIVE -> receive
            ActivityType.SCRIPT -> script
            ActivityType.SERVICE -> service
            else -> throw IllegalStateException("Not supported: $type")
        }
    }
}
