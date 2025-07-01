package com.wutsi.koki.chatbot.messenger

import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.tenant.dto.TenantStatus

object TenantFixtures {
    val tenants = listOf(
        Tenant(
            id = 1,
            name = "test",
            domainName = "localhost",
            locale = "CA",
            currency = "CAD",
            country = "CA",
            currencySymbol = "C\$",
            timeFormat = "hh:mm a",
            dateFormat = "yyyy-MM-dd",
            dateTimeFormat = "yyyy-MM-dd hh:mm a",
            numberFormat = "#,###,##0",
            monetaryFormat = "C\$ #,###,##0.00",
            status = TenantStatus.ACTIVE,
            logoUrl = "https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png",
            iconUrl = "https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png",
            websiteUrl = "https://www.google.com",
            portalUrl = "http://localhost:8080",
            clientPortalUrl = "https://localhost:8082",
        ),
    )
}
