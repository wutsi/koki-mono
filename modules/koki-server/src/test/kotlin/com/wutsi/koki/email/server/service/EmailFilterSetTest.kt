package com.wutsi.koki.email.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class EmailFilterSetTest {
    @Test
    fun filter() {
        val filter1 = mock<EmailFilter>()
        doReturn("A").whenever(filter1).filter(any(), any())

        val filter2 = mock<EmailFilter>()
        doReturn("B").whenever(filter2).filter(any(), any())

        val filter = EmailFilterSet(listOf(filter1, filter2))

        val result = filter.filter("XX", 1)

        verify(filter1).filter("XX", 1)
        verify(filter2).filter("A", 1)
        assertEquals("B", result)
    }
}
