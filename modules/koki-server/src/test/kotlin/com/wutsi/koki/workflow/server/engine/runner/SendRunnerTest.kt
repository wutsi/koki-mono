package com.wutsi.koki.workflow.server.engine.runner

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import com.wutsi.koki.workflow.server.service.runner.EndRunner
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class EndRunnerTest {
    private val engine = mock<WorkflowEngine>()
    private val workflowInstanceService = mock<WorkflowInstanceService>()
    private val executor = EndRunner()
    private val activityInstance = ActivityInstanceEntity(id = "111", workflowInstanceId = "1111", tenantId = 555)

    @Test
    fun run() {
        val workflowInstance = WorkflowInstanceEntity(activityInstance.workflowInstanceId)
        doReturn(workflowInstance).whenever(workflowInstanceService)
            .get(activityInstance.workflowInstanceId, activityInstance.tenantId)

        executor.run(activityInstance, engine)

        verify(engine).done(activityInstance.id!!, emptyMap(), activityInstance.tenantId)
        verify(engine).done(activityInstance.workflowInstanceId, activityInstance.tenantId)
    }
}
