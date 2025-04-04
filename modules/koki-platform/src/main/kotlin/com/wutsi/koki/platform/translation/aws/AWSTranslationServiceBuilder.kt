package com.wutsi.koki.platform.translation.aws

import com.amazonaws.services.translate.AmazonTranslate
import com.amazonaws.services.translate.AmazonTranslateClient
import com.amazonaws.services.translate.model.TranslateTextRequest
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import com.wutsi.koki.platform.translation.TranslationException
import com.wutsi.koki.platform.translation.TranslationService
import java.util.Locale

class AWSTranslationService(
    private val translator: AmazonTranslate
) : TranslationService {
    @Throws(TranslationException::class)
    override fun translate(text: String, language: String): String {
        val request = TranslateTextRequest()
        request.text = text
        request.targetLanguageCode = language

        try {
            val response = translator.translateText(request)
            return response.translatedText
        } catch (ex: Exception) {
            throw TranslationException("Translation failed", ex)
        }
    }
}
