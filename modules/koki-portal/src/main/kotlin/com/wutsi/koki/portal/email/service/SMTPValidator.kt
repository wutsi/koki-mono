package com.wutsi.koki.portal.service

import org.apache.commons.net.smtp.SMTPClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import kotlin.jvm.Throws

@Service
class SMTPValidator {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SMTPValidator::class.java)
    }

    @Throws(IOException::class)
    fun validate(host: String, port: Int, user: String) {
        val smtp = SMTPClient()
        smtp.connectTimeout = 15000 // 15 seconds timeout

        LOGGER.debug("Connecting to $host:$port")
        smtp.connect(host, port)
        try {
            LOGGER.debug("Verifying $user")
            smtp.verify(user)
        } finally {
            smtp.disconnect()
        }
    }
}
