package com.wutsi.koki.portal.pub.common.mapper

import com.wutsi.koki.portal.pub.tenant.service.CurrentTenantHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
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
        return SimpleDateFormat(tenant?.dateTimeFormat ?: "yyyy-MM-dd HH:mm")
    }

    protected fun createDateFormat(): DateFormat {
        val tenant = currentTenant.get()
        return SimpleDateFormat(tenant?.dateFormat ?: "yyyy-MM-dd")
    }

    protected fun createMediumDateFormat(): DateFormat {
        val tenant = currentTenant.get()
        tenant?.locale?.let { locale -> Locale(locale) } ?: LocaleContextHolder.getLocale()
        return DateFormat.getDateInstance(DateFormat.MEDIUM)
    }

    protected fun createTimeFormat(): DateFormat {
        val tenant = currentTenant.get()
        return SimpleDateFormat(tenant?.timeFormat ?: "HH:mm")
    }

    protected fun createNumberFormat(): DecimalFormat {
        val tenant = currentTenant.get()
        return DecimalFormat(tenant?.numberFormat ?: "##.#")
    }

    protected fun createMoneyFormat(): NumberFormat {
        val tenant = currentTenant.get()
        return DecimalFormat(tenant?.monetaryFormat ?: "##.#")
    }
}
