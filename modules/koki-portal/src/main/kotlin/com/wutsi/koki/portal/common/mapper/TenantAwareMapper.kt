package com.wutsi.koki.portal.common.mapper

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.koki.portal.tenant.service.CurrentTenantHolder
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class TenantAwareMapper {
    @Autowired
    protected lateinit var currentTenant: CurrentTenantHolder

    @Autowired
    protected lateinit var currentUser: CurrentUserHolder

    @Autowired
    private lateinit var messages: MessageSource

    protected fun createDateTimeFormat(): DateFormat {
        val tenant = currentTenant.get()
        tenant?.locale?.let { locale -> Locale(locale) } ?: LocaleContextHolder.getLocale()
        return DateFormat.getDateInstance(DateFormat.LONG)
    }

    protected fun createDateFormat(): DateFormat {
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

    protected fun formatMoment(date: Date, dateTimeFormat: DateFormat, timeFormat: DateFormat): String {
        val days = (System.currentTimeMillis() - date.time) / (1000 * 60 * 60 * 24)
        val locale = LocaleContextHolder.getLocale()
        return if (days < 1) {
            messages.getMessage("moment.today", emptyArray(), locale) + " - " + timeFormat.format(date)
        } else if (days < 2) {
            messages.getMessage("moment.yesterday", emptyArray(), locale) + " - " + timeFormat.format(date)
        } else {
            dateTimeFormat.format(date)
        }
    }

    protected fun formatPhoneNumber(number: String, country: String? = null): String {
        try {
            val pnu = PhoneNumberUtil.getInstance()
            val phoneNumber = pnu.parse(number, country ?: "")
            return pnu.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        } catch (ex: NumberParseException) {
            return number
        }
    }
}
