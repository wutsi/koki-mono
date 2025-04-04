package com.wutsi.koki.platform.translation.aws

import com.wutsi.koki.platform.translation.TranslationNotConfiguredException
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class AWSTranslationServiceBuilderTest {
    val config = mapOf(
        ConfigurationName.TRANSLATION_PROVIDER_AWS_REGION to "us-east-1",
        ConfigurationName.TRANSLATION_PROVIDER_AWS_SECRET_KEY to "dx-fdlfldkf",
        ConfigurationName.TRANSLATION_PROVIDER_AWS_ACCESS_KEY to "fdlkfldkdlf"
    )
    val builder = AWSTranslationServiceBuilder()

    @Test
    fun build() {
        val service = builder.build(config)
        assertEquals(true, service is AWSTranslationService)
    }

    @Test
    fun `no region`() {
        assertThrows<TranslationNotConfiguredException> {
            builder.build(config.filter { entry -> entry.key != ConfigurationName.TRANSLATION_PROVIDER_AWS_REGION })
        }
    }

    @Test
    fun `no secret-key`() {
        assertThrows<TranslationNotConfiguredException> {
            builder.build(config.filter { entry -> entry.key != ConfigurationName.TRANSLATION_PROVIDER_AWS_SECRET_KEY })
        }
    }

    @Test
    fun `no access-key`() {
        assertThrows<TranslationNotConfiguredException> {
            builder.build(config.filter { entry -> entry.key != ConfigurationName.TRANSLATION_PROVIDER_AWS_ACCESS_KEY })
        }
    }
}
