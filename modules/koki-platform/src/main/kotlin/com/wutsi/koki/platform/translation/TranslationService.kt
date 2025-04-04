package com.wutsi.koki.platform.translation

interface TranslationService {
    @Throws(TranslationException::class)
    fun translate(text: String, language: String): String
}
