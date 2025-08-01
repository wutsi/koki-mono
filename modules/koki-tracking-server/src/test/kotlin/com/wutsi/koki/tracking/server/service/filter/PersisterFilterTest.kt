package com.wutsi.koki.tracking.server.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.tracking.server.dao.TrackRepository
import com.wutsi.koki.tracking.server.domain.TrackEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertTrue

class PersisterFilterTest {
    companion object {
        const val BUFFER_SIZE = 5
    }

    private lateinit var dao: TrackRepository
    private lateinit var filter: PersisterFilter

    @BeforeEach
    fun setUp() {
        dao = mock()
        filter = PersisterFilter(dao, BUFFER_SIZE)
    }

    @Test
    fun `cache track`() {
        for (i in 1..BUFFER_SIZE - 1) {
            filter.filter(createTrack(i.toString()))
        }

        verify(dao, never()).save(any(), any())
        assertEquals(4, filter.size())
    }

    @Test
    fun `store track`() {
        val tracks = listOf(
            createTrack("1"),
            createTrack("2"),
            createTrack("3"),
            createTrack("4"),
            createTrack("5"),
            createTrack("6"),
            createTrack("7"),
        )
        tracks.forEach {
            filter.filter(it)
        }

        val items = argumentCaptor<List<TrackEntity>>()
        verify(dao).save(any(), items.capture())

        assertTrue(items.firstValue.contains(tracks[0]))
        assertTrue(items.firstValue.contains(tracks[1]))
        assertTrue(items.firstValue.contains(tracks[2]))
        assertTrue(items.firstValue.contains(tracks[3]))
        assertTrue(items.firstValue.contains(tracks[4]))
        assertEquals(2, filter.size())
    }

    @Test
    fun `never store empty buffer`() {
        filter.flush()

        verify(dao, never()).save(any(), any())
    }

    @Test
    fun destroy() {
        val tracks = listOf(
            createTrack("1"),
            createTrack("2"),
        )
        tracks.forEach {
            filter.filter(it)
        }
        filter.destroy()

        val items = argumentCaptor<List<TrackEntity>>()
        verify(dao).save(any(), items.capture())
    }

    private fun createTrack(correlationId: String) = TrackEntity(
        correlationId = correlationId,
    )
}
