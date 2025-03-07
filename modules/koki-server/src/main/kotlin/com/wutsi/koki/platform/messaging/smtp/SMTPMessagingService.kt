package com.wutsi.koki.platform.messaging.smtp

import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingException
import com.wutsi.koki.platform.messaging.MessagingService
import com.wutsi.koki.platform.messaging.Party
import jakarta.mail.Part
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import jdk.internal.joptsimple.internal.Messages.message

class SMTPMessagingService(
    val session: Session,
    val fromAddress: String,
    val fromPersonal: String?,
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

        // Headers
        val senderName = message.sender?.displayName?.ifEmpty { null } ?: fromPersonal
        msg.addFrom(
            arrayOf(InternetAddress(fromAddress, senderName))
        )
        msg.addRecipients(jakarta.mail.Message.RecipientType.TO, arrayOf(toAddress(message.recipient)))
        message.language?.let { lang -> msg.contentLanguage = arrayOf(lang) }

        // Subject
        msg.subject = message.subject

        // Body
        if (message.attachments.isEmpty()) {
            msg.setContent(message.body, message.mimeType)
        } else {
            val parts = MimeMultipart()
            val body = MimeBodyPart()
            body.setContent(message.body, message.mimeType)
            body.setDisposition(Part.INLINE)
            parts.addBodyPart(body)

            // Attachment
            message.attachments.forEach { file ->
                val attachment = MimeBodyPart()
                attachment.attachFile(file)
                parts.addBodyPart(attachment)
            }
            msg.setContent(parts)
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
