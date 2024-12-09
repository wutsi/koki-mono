package com.wutsi.koki.workflow.server.service.runner

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.ActivityRunner
import com.wutsi.koki.workflow.server.engine.WorkflowEngine

abstract class AbstractActivityRunner(
    protected val logger: KVLogger
) : ActivityRunner {
    abstract fun doRun(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine)

    override fun run(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine) {
        logger.add("runner", this::class.simpleName)
        logger.add("activity_instance_id", activityInstance.id)
        logger.add("workflow_instance_id", activityInstance.workflowInstanceId)

        doRun(activityInstance, engine)
    }
}
