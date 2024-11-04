package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.ActivityExecutor
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StartExecutor : ActivityExecutor {
    companion object{
        private val LOGGER = LoggerFactory.getLogger(StartExecutor::class.java)
    }

    override fun execute(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug(">>> ${activityInstance.instance.id} > ${activityInstance.id} executing")
        }
        engine.done(activityInstance)
    }
}
