package com.wutsi.koki.platform.messaging.smtp

import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingService
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.tenant.dto.ConfigurationName
import jakarta.mail.Authenticator
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import java.util.Properties

class SMTPMessagingService(
    private val session: Session,
    private val fromAddress: String,
    private val fromPersonal: String,
) : MessagingService {
    companion object {
        fun of(config: Map<String, String>): SMTPMessagingService {
            val props = Properties()
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.starttls.enable", "true")
            props.put("mail.smtp.port", config.get(ConfigurationName.SMTP_PORT))
            props.put("mail.smtp.host", config.get(ConfigurationName.SMTP_HOST))

            val authenticator = object : Authenticator {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    val username = config.get(ConfigurationName.SMTP_USERNAME)
                    val password = config.get(ConfigurationName.SMTP_PASSWORD)
                    return PasswordAuthentication(username, password);
                }
            }

            return SMTPMessagingService(
                session = Session.getInstance(props, authenticator),
                fromAddress = config.get(ConfigurationName.SMTP_FROM_ADDRESS)!!,
                fromPersonal = config.get(ConfigurationName.SMTP_FROM_PERSONAL)!!
            )
        }
    }

    override fun send(message: Message): String {
        val msg = createMessage(message)
        Transport.send(msg)
        return ""
    }

    private fun createMessage(message: Message): MimeMessage {
        val msg = MimeMessage(session)

        val senderName = message.sender?.displayName?.ifEmpty { null } ?: fromPersonal
        msg.addFrom(arrayOf(InternetAddress(fromAddress, senderName)))

        msg.subject = message.subject
        msg.addRecipients(jakarta.mail.Message.RecipientType.TO, arrayOf(toAddress(message.recipient)))
        msg.setContent(message.body, message.mimeType)
        if (message.language != null) {
            msg.contentLanguage = arrayOf(message.language)
        }

        return msg
    }

    private fun toAddress(party: Party): InternetAddress {
        return if (party.displayName.isNullOrEmpty()) {
            InternetAddress(party.email)
        } else {
            InternetAddress(party.email, party.displayName)
        }
    }
}
