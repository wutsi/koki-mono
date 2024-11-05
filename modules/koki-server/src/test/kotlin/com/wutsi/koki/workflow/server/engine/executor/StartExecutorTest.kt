package com.wutsi.koki.workflow.server.engine

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.service.StartExecutor
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.jvm.java

class StartExecutorTest {
    private val engine = Mockito.mock(WorkflowEngine::class.java)
    private val executor = StartExecutor()
    private val instance = ActivityInstanceEntity()

    @Test
    fun execute() {
        executor.execute(instance, engine)

        verify(engine).done(instance, emptyMap())
    }
}
