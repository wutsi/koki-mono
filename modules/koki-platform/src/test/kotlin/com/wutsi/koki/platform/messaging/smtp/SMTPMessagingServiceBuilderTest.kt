package com.wutsi.koki.platform.messaging.smtp

import com.wutsi.koki.platform.messaging.MessagingNotConfiguredException
import com.wutsi.koki.tenant.dto.ConfigurationName
import jakarta.mail.Session
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class SMTPMessagingServiceBuilderTest {
    private val config = mapOf(
        ConfigurationName.SMTP_TYPE to SMTPType.EXTERNAL.name,
        ConfigurationName.SMTP_PORT to "25",
        ConfigurationName.SMTP_HOST to "smtp.gmail.com",
        ConfigurationName.SMTP_USERNAME to "ray.sponsible",
        ConfigurationName.SMTP_PASSWORD to "secret",
        ConfigurationName.SMTP_FROM_ADDRESS to "no-reply@koki.com",
        ConfigurationName.SMTP_FROM_PERSONAL to "Koki"
    )

    private val session = mock<Session>()
    private val builder = SMTPMessagingServiceBuilder(
        session = session,
        from = "no-reply@xxxx.com"
    )

    @Test
    fun buildDefault() {
        val smtp = builder.build(mapOf())

        assertEquals(null, smtp.fromPersonal)
        assertEquals("no-reply@xxxx.com", smtp.fromAddress)
        assertEquals(session, smtp.session)
    }

    @Test
    fun buildKoki() {
        val smtp = builder.build(
            mapOf(ConfigurationName.SMTP_TYPE to SMTPType.KOKI.name)
        )

        assertEquals(null, smtp.fromPersonal)
        assertEquals("no-reply@xxxx.com", smtp.fromAddress)
        assertEquals(session, smtp.session)
    }

    @Test
    fun buildExternal() {
        val smtp = builder.build(config)

        assertEquals(config[ConfigurationName.SMTP_FROM_PERSONAL], smtp.fromPersonal)
        assertEquals(config[ConfigurationName.SMTP_FROM_ADDRESS], smtp.fromAddress)
        assertEquals(config[ConfigurationName.SMTP_PORT], smtp.session.getProperty("mail.smtp.port"))
        assertEquals(config[ConfigurationName.SMTP_HOST], smtp.session.getProperty("mail.smtp.host"))
        assertEquals("true", smtp.session.getProperty("mail.smtp.starttls.enable"))
        assertEquals("true", smtp.session.getProperty("mail.smtp.auth"))
    }

    @Test
    fun `missing port`() {
        assertThrows<MessagingNotConfiguredException> {
            builder.build(createConfigExcluding(ConfigurationName.SMTP_PORT))
        }
    }

    @Test
    fun `missing host`() {
        assertThrows<MessagingNotConfiguredException> {
            builder.build(createConfigExcluding(ConfigurationName.SMTP_HOST))
        }
    }

    @Test
    fun `missing username`() {
        assertThrows<MessagingNotConfiguredException> {
            builder.build(createConfigExcluding(ConfigurationName.SMTP_USERNAME))
        }
    }

    @Test
    fun `missing password`() {
        assertThrows<MessagingNotConfiguredException> {
            builder.build(createConfigExcluding(ConfigurationName.SMTP_PASSWORD))
        }
    }

    @Test
    fun `missing from-personal`() {
        builder.build(createConfigExcluding(ConfigurationName.SMTP_FROM_PERSONAL))
    }

    @Test
    fun `missing from-address`() {
        assertThrows<MessagingNotConfiguredException> {
            builder.build(createConfigExcluding(ConfigurationName.SMTP_FROM_ADDRESS))
        }
    }

    private fun createConfigExcluding(name: String): Map<String, String> {
        return config.filter { entry -> entry.key != name }
    }
}
