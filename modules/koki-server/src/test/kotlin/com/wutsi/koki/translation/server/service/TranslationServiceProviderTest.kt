package com.wutsi.koki.translation.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.LLMType
import com.wutsi.koki.platform.translation.TranslationProvider
import com.wutsi.koki.platform.translation.TranslationService
import com.wutsi.koki.platform.translation.TranslationServiceBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class TranslationServiceProviderTest {
    private val configurationService = mock<ConfigurationService>()
    private val translationBuilder = mock<TranslationServiceBuilder>()
    private val provider = TranslationServiceProvider(
        configurationService = configurationService,
        translationBuilder = translationBuilder
    )

    private val service = mock<TranslationService>()

    @Test
    fun aws() {
        doReturn(service).whenever(translationBuilder).build(any())

        val config = mapOf(
            ConfigurationName.TRANSLATION_PROVIDER to TranslationProvider.AWS.name,
            ConfigurationName.TRANSLATION_PROVIDER_AWS_REGION to "us-east-1",
            ConfigurationName.TRANSLATION_PROVIDER_AWS_ACCESS_KEY to "ac-43094039040",
            ConfigurationName.TRANSLATION_PROVIDER_AWS_SECRET_KEY to "sk-450905490kdfl",
        )
        doReturn(
            config.map { entry ->
                ConfigurationEntity(name = entry.key, value = entry.value)
            }
        ).whenever(configurationService).search(any(), anyOrNull(), eq("translation."))

        val result = provider.get(111L)

        assertEquals(service, result)
        verify(translationBuilder).build(config.toMutableMap())
    }

    @Test
    fun ai() {
        doReturn(service).whenever(translationBuilder).build(any())

        val config1 = mapOf(
            ConfigurationName.TRANSLATION_PROVIDER to TranslationProvider.AI.name,
        )
        doReturn(
            config1.map { entry ->
                ConfigurationEntity(name = entry.key, value = entry.value)
            }
        ).whenever(configurationService).search(any(), anyOrNull(), eq("translation."))

        val config2 = mapOf(
            ConfigurationName.AI_MODEL to LLMType.DEEPSEEK.name,
            ConfigurationName.AI_MODEL_DEEPSEEK_MODEL to "deepseek-chat",
            ConfigurationName.AI_MODEL_DEEPSEEK_API_KEY to "sk-540905490549",
        )
        doReturn(
            config2.map { entry ->
                ConfigurationEntity(name = entry.key, value = entry.value)
            }
        ).whenever(configurationService).search(any(), anyOrNull(), eq("ai."))

        val result = provider.get(111L)

        assertEquals(service, result)

        val config = mutableMapOf<String, String>()
        config.putAll(config1)
        config.putAll(config2)
        verify(translationBuilder).build(config.toMutableMap())
    }
}
