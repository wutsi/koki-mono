package com.wutsi.koki.workflow.server.engine.runner

import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.service.runner.ManualRunner
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.jvm.java

class ManualRunnerTest {
    private val engine = Mockito.mock(WorkflowEngine::class.java)
    private val logger = DefaultKVLogger()
    private val executor = ManualRunner(logger)
    private val instance = ActivityInstanceEntity()

    @Test
    fun run() {
        executor.run(instance, engine)
        // Nothing happends
    }
}
