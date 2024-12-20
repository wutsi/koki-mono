package com.wutsi.koki.workflow.server.engine.runner

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import com.wutsi.koki.workflow.server.service.runner.EndRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class EndRunnerTest {
    private val engine = mock<WorkflowEngine>()
    private val workflowInstanceService = mock<WorkflowInstanceService>()
    private val logger = DefaultKVLogger()
    private val executor = EndRunner(logger)
    private val activityInstance = ActivityInstanceEntity(
        id = "111",
        workflowInstanceId = "1111",
        tenantId = 555,
        status = WorkflowStatus.RUNNING,
    )
    private val workflowInstance = WorkflowInstanceEntity(activityInstance.workflowInstanceId)

    @BeforeEach
    fun setUp() {
        doReturn(workflowInstance).whenever(workflowInstanceService)
            .get(activityInstance.workflowInstanceId, activityInstance.tenantId)
    }

    @Test
    fun run() {
        executor.run(activityInstance, engine)

        verify(engine).done(activityInstance.id!!, emptyMap(), activityInstance.tenantId)
        verify(engine).done(activityInstance.workflowInstanceId, activityInstance.tenantId)
    }

    @Test
    fun `not running`() {
        executor.run(activityInstance.copy(status = WorkflowStatus.DONE), engine)

        verify(engine, never()).done(any(), any(), any())
        verify(engine, never()).done(any(), any())
    }
}
