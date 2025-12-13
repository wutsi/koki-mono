package com.wutsi.koki.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.translate.AmazonTranslate
import com.amazonaws.services.translate.AmazonTranslateClientBuilder
import com.wutsi.koki.platform.ai.llm.LLMBuilder
import com.wutsi.koki.platform.translation.TranslationServiceBuilder
import com.wutsi.koki.platform.translation.ai.AITranslationBuilder
import com.wutsi.koki.platform.translation.aws.AWSTranslationHealthIndicator
import com.wutsi.koki.platform.translation.aws.AWSTranslationServiceBuilder
import org.apache.tika.language.detect.LanguageDetector
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TranslationConfiguration(
    private val llmBuilder: LLMBuilder,

    @param:Value("\${koki.translation.aws.region}") private val awsRegion: String,
    @param:Value("\${koki.translation.aws.access-key}") private val awsAccessKey: String,
    @param:Value("\${koki.translation.aws.secret-key}") private val awsSecretKey: String,
) {
    @Bean
    fun getLanguageDetector(): LanguageDetector {
        return LanguageDetector.getDefaultLanguageDetector().loadModels()
    }

    @Bean
    open fun translationServiceBuilder(): TranslationServiceBuilder {
        return TranslationServiceBuilder(
            aws = awsTranslationBuilder(),
            ai = aiTransactionBuilder(),
        )
    }

    @Bean
    open fun awsTranslationBuilder(): AWSTranslationServiceBuilder {
        return AWSTranslationServiceBuilder()
    }

    @Bean
    open fun aiTransactionBuilder(): AITranslationBuilder {
        return AITranslationBuilder(llmBuilder)
    }

    @Bean
    fun translateClient(): AmazonTranslate {
        return AmazonTranslateClientBuilder.standard()
            .withRegion(awsRegion)
            .withCredentials(
                AWSStaticCredentialsProvider(
                    BasicAWSCredentials(awsAccessKey, awsSecretKey)
                )
            ).build()
    }

    @Bean
    @ConditionalOnProperty(
        value = ["koki.translation"],
        havingValue = "aws",
        matchIfMissing = false,
    )
    open fun translationHealthIndicator(): HealthIndicator {
        return AWSTranslationHealthIndicator(
            translator = translateClient()
        )
    }
}
