package com.wutsi.koki.tracking.server.service

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.tracking.server.domain.TrackEntity
import kotlin.test.Test

class PipelineTest {
    @Test
    fun filter() {
        val track = TrackEntity()

        val step1 = mock<Filter>()
        doReturn(track).whenever(step1).filter(track)

        val step2 = mock<Filter>()
        doReturn(track).whenever(step2).filter(track)

        val pipeline = Pipeline(listOf(step1, step2))

        pipeline.filter(track)

        verify(step1).filter(track)
        verify(step2).filter(track)
    }
}
