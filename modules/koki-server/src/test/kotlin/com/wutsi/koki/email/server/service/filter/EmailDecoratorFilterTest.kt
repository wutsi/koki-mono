package com.wutsi.koki.email.server.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.email.server.service.EmailDecoratorService
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class EmailDecoratorFilterTest {
    private val decorator = mock<EmailDecoratorService>()
    private val filter = EmailDecoratorFilter(decorator)

    @Test
    fun filter() {
        doReturn("YYY").whenever(decorator).decorate(any(), any())

        val result = filter.filter("XXX", 1L)

        assertEquals("YYY", result)
    }
}
