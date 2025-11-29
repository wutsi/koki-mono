package com.wutsi.koki.portal.pub.common.mapper

import com.wutsi.koki.portal.pub.tenant.service.CurrentTenantHolder
import org.springframework.beans.factory.annotation.Autowired
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

abstract class TenantAwareMapper {
    @Autowired
    protected lateinit var currentTenant: CurrentTenantHolder

    protected fun createDateTimeFormat(): DateFormat {
        val tenant = currentTenant.get()
        return SimpleDateFormat(tenant.dateTimeFormat)
    }

    protected fun createDateFormat(): DateFormat {
        val tenant = currentTenant.get()
        return SimpleDateFormat(tenant.dateFormat)
    }

    protected fun createMediumDateFormat(): DateFormat {
        val tenant = currentTenant.get()
        tenant.locale.let { locale -> Locale(locale) }
        return DateFormat.getDateInstance(DateFormat.MEDIUM)
    }

    protected fun createMoneyFormat(): NumberFormat {
        val tenant = currentTenant.get()
        return DecimalFormat(tenant.monetaryFormat)
    }
}
