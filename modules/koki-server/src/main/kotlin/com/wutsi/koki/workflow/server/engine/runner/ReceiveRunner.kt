package com.wutsi.koki.workflow.server.service.runner

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import org.springframework.stereotype.Service

@Service
class ReceiveRunner(logger: KVLogger) : AbstractActivityRunner(logger) {
    override fun doRun(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine) {
    }
}
