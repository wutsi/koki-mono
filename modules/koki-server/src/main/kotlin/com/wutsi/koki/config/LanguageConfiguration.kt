package com.wutsi.koki.config

import org.apache.tika.language.detect.LanguageDetector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LanguageConfiguration {
    @Bean
    fun getLanguageDetector(): LanguageDetector {
        return LanguageDetector.getDefaultLanguageDetector().loadModels()
    }
}
