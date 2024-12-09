package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.server.service.runner.EndRunner
import com.wutsi.koki.workflow.server.service.runner.ManualRunner
import com.wutsi.koki.workflow.server.service.runner.ReceiveRunner
import com.wutsi.koki.workflow.server.service.runner.ScriptRunner
import com.wutsi.koki.workflow.server.service.runner.SendRunner
import com.wutsi.koki.workflow.server.service.runner.StartRunner
import com.wutsi.koki.workflow.server.service.runner.UserRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import java.lang.IllegalStateException
import kotlin.jvm.java
import kotlin.test.assertEquals

class ActivityRunnerProviderTest {
    private val start = mock(StartRunner::class.java)
    private val stop = mock(EndRunner::class.java)
    private val manual = mock(ManualRunner::class.java)
    private val user = mock(UserRunner::class.java)
    private val send = mock(SendRunner::class.java)
    private val receive = mock(ReceiveRunner::class.java)
    private val script = mock(ScriptRunner::class.java)
    private val provider = ActivityRunnerProvider(start, stop, manual, user, send, receive, script)

    @Test
    fun start() {
        assertEquals(start, provider.get(ActivityType.START))
    }

    @Test
    fun stop() {
        assertEquals(stop, provider.get(ActivityType.END))
    }

    @Test
    fun manual() {
        assertEquals(manual, provider.get(ActivityType.MANUAL))
    }

    @Test
    fun user() {
        assertEquals(user, provider.get(ActivityType.USER))
    }

    @Test
    fun send() {
        assertEquals(send, provider.get(ActivityType.SEND))
    }

    @Test
    fun receive() {
        assertEquals(receive, provider.get(ActivityType.RECEIVE))
    }

    @Test
    fun script() {
        assertEquals(script, provider.get(ActivityType.SCRIPT))
    }

    @Test
    fun unknown() {
        assertThrows<IllegalStateException> {
            provider.get(ActivityType.UNKNOWN)
        }
    }
}
