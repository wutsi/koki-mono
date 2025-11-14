package com.wutsi.koki.tracking.server.job

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.tracking.server.service.KpiListingGenerator
import org.mockito.Mockito.mock
import java.time.LocalDate
import kotlin.test.Test

class KpiListingGeneratorJobTest {
    private val generator = mock<KpiListingGenerator>()
    private val job = KpiListingGeneratorJob(generator)

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
