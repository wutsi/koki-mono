package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import org.springframework.stereotype.Service

@Service
class StartExecutor : ActivityExecutor {
    override fun execute(activityInstance: ActivityInstanceEntity) {
    }
}
