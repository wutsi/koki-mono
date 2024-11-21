package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.service.UserExecutor
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.jvm.java

class UserExecutorTest {
    private val engine = Mockito.mock(WorkflowEngine::class.java)
    private val executor = UserExecutor()
    private val instance = ActivityInstanceEntity()

    @Test
    fun execute() {
        executor.execute(instance, engine)
        // Nothing happends
    }
}
