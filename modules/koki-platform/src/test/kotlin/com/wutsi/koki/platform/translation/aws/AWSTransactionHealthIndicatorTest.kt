package com.wutsi.koki.platform.translation.aws

import com.amazonaws.services.translate.AmazonTranslate
import com.amazonaws.services.translate.model.TranslateTextResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import org.mockito.Mockito.mock
import org.springframework.boot.health.contributor.Status
import kotlin.test.Test
import kotlin.test.assertEquals

class AWSTransactionHealthIndicatorTest {
    private val translator = mock<AmazonTranslate>()
    private val indicator = AWSTranslationHealthIndicator(translator)

    @Test
    fun translate() {
        doReturn(TranslateTextResult()).whenever(translator).translateText(any())

        assertEquals(Status.UP, indicator.health().status)
    }

    @Test
    fun error() {
        doThrow(RuntimeException::class).whenever(translator).translateText(any())

        assertEquals(Status.DOWN, indicator.health().status)
    }
}
