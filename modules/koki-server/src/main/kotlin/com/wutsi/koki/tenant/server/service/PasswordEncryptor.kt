package com.wutsi.koki.tenant.server.service

import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service

@Service
class PasswordService {
    fun hash(clear: String, salt: String): String {
        return DigestUtils.md5Hex("$clear-$salt")
    }

    fun matches(clear: String, hashed: String, salt: String): Boolean {
        return hashed.equals(hash(clear, salt))
    }
}
