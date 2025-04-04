package com.wutsi.koki.platform.translation

import com.wutsi.koki.platform.translation.ai.AITranslationBuilder
import com.wutsi.koki.platform.translation.aws.AWSTranslationServiceBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName

class TranslationServiceBuilder(
    private val ai: AITranslationBuilder, private val aws: AWSTranslationServiceBuilder
) {
    fun build(config: Map<String, String>): TranslationService {
        val provider = config[ConfigurationName.TRANSLATION_PROVIDER]
        return when (provider?.uppercase()) {
            TranslationProvider.AI.name -> ai.build(config)
            TranslationProvider.AWS.name -> aws.build(config)
            else -> throw TranslationNotConfiguredException("Provider not supported: $provider")
        }
    }
}
