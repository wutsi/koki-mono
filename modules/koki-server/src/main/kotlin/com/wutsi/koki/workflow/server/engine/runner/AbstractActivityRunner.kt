package com.wutsi.koki.workflow.server.service.runner

import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.ActivityRunner
import com.wutsi.koki.workflow.server.engine.WorkflowEngine

abstract class AbstractActivityRunner : ActivityRunner {
    abstract fun run(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine, logger: KVLogger)

    override fun run(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine) {
        val logger = DefaultKVLogger()
        logger.add("runner", this::class.simpleName)
        logger.add("activity_instance_id", activityInstance.id)
        logger.add("workflow_instance_id", activityInstance.workflowInstanceId)
        try {
            run(activityInstance, engine, logger)
        } catch (ex: Exception) {
            logger.setException(ex)
        } finally {
            logger.log()
        }
    }
}
