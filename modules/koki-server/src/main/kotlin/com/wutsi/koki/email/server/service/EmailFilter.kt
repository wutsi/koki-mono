package com.wutsi.koki.email.server.service

interface EmailFilter {
    fun filter(html: String, tenantId: Long): String
}
