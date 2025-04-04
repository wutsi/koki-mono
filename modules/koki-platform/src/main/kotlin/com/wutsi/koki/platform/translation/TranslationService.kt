package com.wutsi.koki.platform.translation

interface TranslationService {
    fun translate(text: String, language: String): String?
}
