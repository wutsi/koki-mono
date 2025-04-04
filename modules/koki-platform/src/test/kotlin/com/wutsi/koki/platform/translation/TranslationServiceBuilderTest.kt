package com.wutsi.koki.platform.translation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.translation.ai.AITranslationBuilder
import com.wutsi.koki.platform.translation.ai.AITranslationService
import com.wutsi.koki.platform.translation.aws.AWSTranslationServiceBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class TranslationServiceBuilderTest {
    private val aiBuilder = mock<AITranslationBuilder>()
    private val awsBuilder = mock<AWSTranslationServiceBuilder>()
    private val builder = TranslationServiceBuilder(
        ai = aiBuilder,
        aws = awsBuilder,
    )

    @Test
    fun ai() {
        val service = mock<AITranslationService>()
        doReturn(service).whenever(aiBuilder).build(any())

        val config = mapOf(ConfigurationName.TRANSLATION_PROVIDER to TranslationProvider.AI.name)
        val result = builder.build(config)

        assertEquals(service, result)
    }

    @Test
    fun aws() {
        val service = mock<AITranslationService>()
        doReturn(service).whenever(awsBuilder).build(any())

        val config = mapOf(ConfigurationName.TRANSLATION_PROVIDER to TranslationProvider.AWS.name)
        val result = builder.build(config)

        assertEquals(service, result)
    }

    @Test
    fun error() {
        val service = mock<AITranslationService>()
        doReturn(service).whenever(awsBuilder).build(any())

        val config = mapOf(ConfigurationName.TRANSLATION_PROVIDER to "xxx")
        assertThrows<TranslationNotConfiguredException> { builder.build(config) }
    }
}
