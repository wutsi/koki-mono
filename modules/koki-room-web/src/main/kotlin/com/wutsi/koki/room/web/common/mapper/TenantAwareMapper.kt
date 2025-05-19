package com.wutsi.koki.room.web.common.mapper

import com.wutsi.koki.room.web.tenant.service.CurrentTenantHolder
import org.springframework.beans.factory.annotation.Autowired
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

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
