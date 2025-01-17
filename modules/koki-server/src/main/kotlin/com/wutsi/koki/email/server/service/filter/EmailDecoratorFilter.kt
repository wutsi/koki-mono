package com.wutsi.koki.email.server.service.filter

import com.wutsi.koki.email.server.service.EmailDecoratorService
import com.wutsi.koki.email.server.service.EmailFilter
import org.springframework.stereotype.Service

@Service
class EmailDecoratorFilter(
    private val decorator: EmailDecoratorService
) : EmailFilter {
    override fun filter(html: String, tenantId: Long): String {
        return decorator.decorate(html, tenantId)
    }
}
