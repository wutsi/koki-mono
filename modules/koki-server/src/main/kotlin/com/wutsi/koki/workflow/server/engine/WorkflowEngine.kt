package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity

interface WorkflowEngine {
    /**
     * Execute an activity
     */
    fun execute(activity: ActivityInstanceEntity): Boolean

    /**
     * Change the status of the activity to DONE and run the next activities
     */
    fun done(activity: ActivityInstanceEntity)
}
