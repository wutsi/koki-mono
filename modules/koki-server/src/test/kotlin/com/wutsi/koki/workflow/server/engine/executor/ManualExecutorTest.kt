package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.service.ManualExecutor
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.jvm.java

class ManualExecutorTest {
    private val engine = Mockito.mock(WorkflowEngine::class.java)
    private val executor = ManualExecutor()
    private val instance = ActivityInstanceEntity()

    @Test
    fun execute() {
        executor.execute(instance, engine)
        // Nothing happends
    }
}
