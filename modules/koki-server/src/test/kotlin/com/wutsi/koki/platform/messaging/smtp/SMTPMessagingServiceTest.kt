package com.wutsi.koki.platform.messaging.smtp

import com.wutsi.koki.tenant.dto.ConfigurationName
import kotlin.test.assertEquals
import kotlin.test.Test
import org.junit.jupiter.api.assertThrows

class SMTPMessagingServiceBuilderTest {
    private val config = mapOf(
        ConfigurationName.SMTP_PORT to "25",
        ConfigurationName.SMTP_HOST to "smtp.gmail.com",
        ConfigurationName.SMTP_USERNAME to "ray.sponsible",
        ConfigurationName.SMTP_PASSWORD to "secret",
        ConfigurationName.SMTP_FROM_ADDRESS to "no-reply@koki.com",
        ConfigurationName.SMTP_FROM_PERSONAL to "Koki"
    )
    private val builder = SMTPMessagingServiceBuilder()

    @Test
    fun build() {
        val smtp = builder.build(config)

        assertEquals("Koki", smtp.fromPersonal)
        assertEquals("no-reply@koki.com", smtp.fromAddress)
        assertEquals(config[ConfigurationName.SMTP_PORT], smtp.session.getProperty("mail.smtp.port"))
        assertEquals(config[ConfigurationName.SMTP_HOST], smtp.session.getProperty("mail.smtp.host"))
        assertEquals("true", smtp.session.getProperty("mail.smtp.starttls.enable"))
        assertEquals("true", smtp.session.getProperty("mail.smtp.auth"))
    }

    @Test
    fun `missing port`() {
        assertThrows<IllegalStateException> {
            builder.build(createConfigExcluding(ConfigurationName.SMTP_PORT))
        }
    }

    @Test
    fun `missing host`() {
        assertThrows<IllegalStateException> {
            builder.build(createConfigExcluding(ConfigurationName.SMTP_HOST))
        }
    }

    @Test
    fun `missing username`() {
        assertThrows<IllegalStateException> {
            builder.build(createConfigExcluding(ConfigurationName.SMTP_USERNAME))
        }
    }

    @Test
    fun `missing password`() {
        assertThrows<IllegalStateException> {
            builder.build(createConfigExcluding(ConfigurationName.SMTP_PASSWORD))
        }
    }

    @Test
    fun `missing from-personal`() {
        assertThrows<IllegalStateException> {
            builder.build(createConfigExcluding(ConfigurationName.SMTP_FROM_PERSONAL))
        }
    }

    @Test
    fun `missing from-address`() {
        assertThrows<IllegalStateException> {
            builder.build(createConfigExcluding(ConfigurationName.SMTP_FROM_ADDRESS))
        }
    }

    private fun createConfigExcluding(name: String): Map<String, String> {
        return config.filter { entry -> entry.key != name }
    }
}
