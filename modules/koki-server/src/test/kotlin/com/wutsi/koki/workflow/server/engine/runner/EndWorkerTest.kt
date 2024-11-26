package com.wutsi.koki.workflow.server.engine

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.service.EndWorker
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class EndWorkerTest {
    private val engine = mock<WorkflowEngine>()
    private val workflowInstanceService = mock<WorkflowInstanceService>()
    private val executor = EndWorker(workflowInstanceService)
    private val activityInstance = ActivityInstanceEntity(workflowInstanceId = "1111", tenantId = 555)

    @Test
    fun execute() {
        val workflowInstance = WorkflowInstanceEntity(activityInstance.workflowInstanceId)
        doReturn(workflowInstance).whenever(workflowInstanceService)
            .get(activityInstance.workflowInstanceId, activityInstance.tenantId)

        executor.execute(activityInstance, engine)

        verify(engine).done(activityInstance, emptyMap())
        verify(engine).done(workflowInstance)
    }
}
