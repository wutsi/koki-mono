package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.server.service.EndExecutor
import com.wutsi.koki.workflow.server.service.ManualExecutor
import com.wutsi.koki.workflow.server.service.StartExecutor
import com.wutsi.koki.workflow.server.service.UserExecutor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.lang.IllegalStateException
import kotlin.jvm.java
import kotlin.test.assertEquals

class ActivityExecutorProviderTest {
    private val start = Mockito.mock(StartExecutor::class.java)
    private val stop = Mockito.mock(EndExecutor::class.java)
    private val manual = Mockito.mock(ManualExecutor::class.java)
    private val user = Mockito.mock(UserExecutor::class.java)
    private val provider = ActivityExecutorProvider(start, stop, manual, user)

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
    fun unknown() {
        assertThrows<IllegalStateException> {
            provider.get(ActivityType.UNKNOWN)
        }
    }
}
