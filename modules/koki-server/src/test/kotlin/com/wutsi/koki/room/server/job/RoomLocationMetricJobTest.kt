package com.wutsi.koki.room.server.job

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.room.server.service.RoomLocationMetricService
import org.mockito.Mockito.mock
import kotlin.test.Test

class RoomLocationMetricJobTest {
    private val service = mock<RoomLocationMetricService>()
    private val job = RoomLocationMetricJob(service)

    @Test
    fun run() {
        job.run()

        verify(service).compile()
    }
}
