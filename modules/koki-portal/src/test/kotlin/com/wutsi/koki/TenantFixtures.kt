package com.wutsi.koki

import com.wutsi.koki.ModuleFixtures.modules
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
            currencySymbol = "C\$",
            timeFormat = "hh:mm a",
            dateFormat = "yyyy-MM-dd",
            dateTimeFormat = "yyyy-MM-dd hh:mm a",
            numberFormat = "#,###,##0",
            monetaryFormat = "C\$ #,###,##0.00",
            status = TenantStatus.ACTIVE,
            logoUrl = "https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png",
            iconUrl = "https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png",
            moduleIds = modules.map { module -> module.id }
        ),
        Tenant(
            id = 2,
            name = "test.cm",
            domainName = "test.cm",
            locale = "fr_CM",
            currency = "XAF",
            currencySymbol = "FCFA",
            timeFormat = "HH:mm",
            dateFormat = "yyyy-MM-dd",
            dateTimeFormat = "yyyy-MM-dd HH:mm",
            numberFormat = "#,###,###.#0",
            monetaryFormat = "#,###,### FCFA",
            status = TenantStatus.ACTIVE,
            moduleIds = modules.map { module -> module.id }
        ),
    )
}
