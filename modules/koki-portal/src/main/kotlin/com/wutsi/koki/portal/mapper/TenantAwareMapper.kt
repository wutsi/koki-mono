package com.wutsi.koki.portal.mapper

import com.wutsi.koki.portal.service.CurrentTenantHolder
import org.springframework.beans.factory.annotation.Autowired
import java.text.DateFormat
import java.text.SimpleDateFormat

abstract class TenantAwareMapper {
    @Autowired
    protected lateinit var currentTenant: CurrentTenantHolder

    protected fun createDateFormat(): DateFormat {
        val tenant = currentTenant.get()
        return SimpleDateFormat(tenant?.dateTimeFormat ?: "yyyy-MM-dd HH:mm")
    }
}
