package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.service.UserWorker
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.jvm.java

class UserWorkerTest {
    private val engine = Mockito.mock(WorkflowEngine::class.java)
    private val executor = UserWorker()
    private val instance = ActivityInstanceEntity()

    @Test
    fun execute() {
        executor.execute(instance, engine)
        // Nothing happends
    }
}
