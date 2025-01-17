package com.wutsi.koki.email.server.service

class EmailFilterSet(private val filters: List<EmailFilter>) : EmailFilter {
    override fun filter(html: String, tenantId: Long): String {
        var xhtml = html
        filters.forEach { filter -> xhtml = filter.filter(xhtml, tenantId) }
        return xhtml
    }
}
