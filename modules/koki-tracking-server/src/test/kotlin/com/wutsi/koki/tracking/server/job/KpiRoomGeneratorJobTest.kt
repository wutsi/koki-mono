package com.wutsi.koki.tracking.server.job

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.tracking.server.service.KpiRoomGenerator
import org.mockito.Mockito.mock
import java.time.LocalDate
import kotlin.test.Test

class KpiRoomGeneratorJobTest {
    private val generator = mock<KpiRoomGenerator>()
    private val job = KpiRoomGeneratorJob(generator)

    @Test
    fun daily() {
        job.daily()
        verify(generator).generate(LocalDate.now())
    }

    @Test
    fun monthly() {
        job.monthly()
        verify(generator).generate(LocalDate.now().minusMonths(1))
    }
}
