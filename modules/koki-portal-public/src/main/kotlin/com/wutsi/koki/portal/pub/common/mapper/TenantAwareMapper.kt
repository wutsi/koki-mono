package com.wutsi.koki.portal.pub.common.mapper

import com.wutsi.koki.portal.pub.tenant.service.CurrentTenantHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

abstract class TenantAwareMapper {
    @Autowired
    protected lateinit var currentTenant: CurrentTenantHolder

    protected fun getLocale(): Locale {
        val tenant = currentTenant.get()
        return Locale(tenant.country, LocaleContextHolder.getLocale().language)
    }

    protected fun createDateTimeFormat(): DateFormat {
        val locale = getLocale()
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale)
    }

    protected fun createDateFormat(): DateFormat {
        val locale = getLocale()
        return DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
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
