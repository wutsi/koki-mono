package com.wutsi.koki.workflow.server.service.runner

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import org.springframework.stereotype.Service

@Service
class StartRunner : AbstractActivityRunner() {
    override fun run(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine, logger: KVLogger) {
        engine.done(activityInstance.id!!, emptyMap(), activityInstance.tenantId)
    }
}
