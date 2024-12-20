package com.wutsi.koki.workflow.server.engine.runner

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.service.runner.StartRunner
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.jvm.java

class StartRunnerTest {
    private val engine = Mockito.mock(WorkflowEngine::class.java)
    private val logger = DefaultKVLogger()
    private val executor = StartRunner(logger)
    private val instance = ActivityInstanceEntity(id = "111", tenantId = 555L, status = WorkflowStatus.RUNNING)

    @Test
    fun run() {
        executor.run(instance, engine)

        verify(engine).done(instance.id!!, emptyMap(), instance.tenantId)
    }

    @Test
    fun `not running`() {
        executor.run(instance.copy(status = WorkflowStatus.DONE), engine)

        verify(engine, never()).done(any(), any(), any())
    }
}
