package com.wutsi.koki.platform.messaging.smtp

import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingException
import com.wutsi.koki.platform.messaging.MessagingService
import com.wutsi.koki.platform.messaging.Party
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage

class SMTPMessagingService(
    val session: Session,
    val fromAddress: String,
    val fromPersonal: String,
) : MessagingService {
    override fun send(message: Message): String {
        try {
            val msg = createMessage(message)
            Transport.send(msg)
            return ""
        } catch (ex: Exception) {
            throw MessagingException("Delivery failure", ex)
        }
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
