package com.wutsi.koki.platform.translation

import com.amazonaws.services.translate.AmazonTranslate
import com.amazonaws.services.translate.model.TranslateTextRequest
import com.amazonaws.services.translate.model.TranslateTextResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class AWSTransactionServiceTest {
    private val translator = mock<AmazonTranslate>()
    val service = AWSTranslationService(translator)

    @Test
    fun translate() {
        val response = TranslateTextResult()
        response.translatedText = "Bonjour le monde"
        doReturn(response).whenever(translator).translateText(any())

        val result = service.translate("Hello world", "fr")
        assertEquals(response.translatedText, result)

        val request = argumentCaptor<TranslateTextRequest>()
        verify(translator).translateText(request.capture())
        assertEquals("Hello world", request.firstValue.text)
        assertEquals("fr", request.firstValue.targetLanguageCode)
    }

    @Test
    fun error() {
        doThrow(RuntimeException::class).whenever(translator).translateText(any())

        assertThrows<TranslationException> { service.translate("Hello world", "fr") }
    }
}
