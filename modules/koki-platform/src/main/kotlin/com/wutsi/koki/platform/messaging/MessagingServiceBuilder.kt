package com.wutsi.koki.platform.messaging

import com.wutsi.koki.platform.messaging.smtp.SMTPMessagingServiceBuilder

class MessagingServiceBuilder(
    private val smtpBuilder: SMTPMessagingServiceBuilder,
) {
    @Throws(MessagingNotConfiguredException::class)
    fun build(config: Map<String, String>): MessagingService {
        return smtpBuilder.build(config)
    }
}
