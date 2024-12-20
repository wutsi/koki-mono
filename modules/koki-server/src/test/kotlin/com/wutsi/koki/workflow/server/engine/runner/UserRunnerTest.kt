package com.wutsi.koki.workflow.server.engine.runner

import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.service.runner.UserRunner
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.jvm.java

class UserRunnerTest {
    private val engine = Mockito.mock(WorkflowEngine::class.java)
    private val logger = DefaultKVLogger()
    private val executor = UserRunner(logger)
    private val instance = ActivityInstanceEntity(status = WorkflowStatus.RUNNING)

    @Test
    fun run() {
        executor.run(instance, engine)
        // Nothing happends
    }
}
