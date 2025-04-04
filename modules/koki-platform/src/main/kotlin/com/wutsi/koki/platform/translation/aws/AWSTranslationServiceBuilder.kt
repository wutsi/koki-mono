package com.wutsi.koki.platform.translation.aws

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.translate.AmazonTranslateClientBuilder
import com.wutsi.koki.platform.translation.TranslationNotConfiguredException
import com.wutsi.koki.platform.translation.TranslationService
import com.wutsi.koki.tenant.dto.ConfigurationName

class AWSTranslationServiceBuilder {
    companion object {
        val CONFIG_NAMES = listOf(
            ConfigurationName.TRANSLATION_PROVIDER_AWS_REGION,
            ConfigurationName.TRANSLATION_PROVIDER_AWS_ACCESS_KEY,
            ConfigurationName.TRANSLATION_PROVIDER_AWS_SECRET_KEY,
        )
    }

    fun build(config: Map<String, String>): TranslationService {
        validate(config)
        val client = AmazonTranslateClientBuilder.standard()
            .withRegion(config[ConfigurationName.TRANSLATION_PROVIDER_AWS_REGION]!!)
            .withCredentials(
                AWSStaticCredentialsProvider(
                    BasicAWSCredentials(
                        config[ConfigurationName.TRANSLATION_PROVIDER_AWS_ACCESS_KEY]!!,
                        config[ConfigurationName.TRANSLATION_PROVIDER_AWS_SECRET_KEY]!!,
                    )
                )
            )
            .build()
        return AWSTranslationService(client)
    }

    private fun validate(config: Map<String, String>) {
        val missing = CONFIG_NAMES.filter { name -> config[name].isNullOrEmpty() }
        if (missing.isNotEmpty()) {
            throw TranslationNotConfiguredException("Translation not configured. Missing config: $missing")
        }
    }
}
