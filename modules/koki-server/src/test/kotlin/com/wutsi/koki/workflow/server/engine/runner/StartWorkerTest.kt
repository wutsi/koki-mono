package com.wutsi.koki.workflow.server.engine.runner

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.service.runner.StartRunner
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.jvm.java

class StartWorkerTest {
    private val engine = Mockito.mock(WorkflowEngine::class.java)
    private val executor = StartRunner()
    private val instance = ActivityInstanceEntity()

    @Test
    fun run() {
        executor.run(instance, engine)

        verify(engine).done(instance, emptyMap())
    }
}
