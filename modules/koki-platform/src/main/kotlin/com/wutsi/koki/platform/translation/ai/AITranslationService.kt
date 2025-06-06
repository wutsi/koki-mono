package com.wutsi.koki.platform.translation.ai

import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import com.wutsi.koki.platform.translation.TranslationException
import com.wutsi.koki.platform.translation.TranslationService
import java.util.Locale

class AITranslationService(
    private val llm: LLM
) : TranslationService {
    companion object {
        const val SYSTEM_INSTRUCTIONS = """
            You are a helpful translation assistant. When I provide text, you will translate it to the specified language.

            Instructions:
            - Return only the translation, nothing else.
            - Do not translate text enclosed in "{{" and "}}" as they represent variables. Example: {{orderNumber}} is the variable that represent teh order number.
            - If the text contains HTML tags, keep them as is.
            - If you can't translate, just return an empty string (nothing else)
            - Use professional language for business communication.
        """

        const val PROMPT = """
            Can you translate the following text to {{language}}:
            {{text}}
        """
    }

    @Throws(TranslationException::class)
    override fun translate(text: String, language: String): String {
        val prompt = PROMPT
            .replace("{{language}}", Locale(language).getDisplayLanguage(Locale.ENGLISH))
            .replace("{{text}}", text)

        try {
            val response = llm.generateContent(
                LLMRequest(
                    messages = listOf(
                        Message(role = Role.SYSTEM, text = SYSTEM_INSTRUCTIONS.trimIndent()),
                        Message(role = Role.USER, text = prompt.trimIndent()),
                    )
                )
            )
            return response.messages
                .firstOrNull()
                ?.text
                ?.ifEmpty { null }
                ?: throw TranslationException("None")
        } catch (ex: Exception) {
            throw TranslationException("Translation failed", ex)
        }
    }
}
