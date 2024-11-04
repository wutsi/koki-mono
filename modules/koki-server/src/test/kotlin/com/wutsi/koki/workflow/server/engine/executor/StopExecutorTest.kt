package com.wutsi.koki.workflow.server.engine

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.service.StopExecutor
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.jvm.java

class StopExecutorTest {
    private val engine = Mockito.mock(WorkflowEngine::class.java)
    private val executor = StopExecutor()
    private val activityInstance = ActivityInstanceEntity(instance = WorkflowInstanceEntity())

    @Test
    fun execute() {
        executor.execute(activityInstance, engine)

        verify(engine).done(activityInstance)
        verify(engine).stop(activityInstance.instance)
    }
}
