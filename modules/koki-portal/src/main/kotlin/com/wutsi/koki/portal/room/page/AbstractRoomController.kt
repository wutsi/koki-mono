package com.wutsi.koki.portal.room.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController
import com.wutsi.koki.room.dto.LeaseTerm
import org.apache.commons.lang3.time.DateUtils
import org.springframework.ui.Model
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

abstract class AbstractRoomController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "room"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }

    protected fun loadCheckinCheckoutTime(model: Model) {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val fmt = SimpleDateFormat("HH:mm")
        val checkInOutTimes = mutableListOf<String>()
        (0..23).forEach { i ->
            val cur = DateUtils.addHours(cal.time, i)
            checkInOutTimes.add(fmt.format(cur))
        }
        model.addAttribute("checkInOutTimes", checkInOutTimes)
    }

    protected fun loadLeaseTermDurations(model: Model) {
        model.addAttribute("leaseTerms", LeaseTerm.entries)
        model.addAttribute("leaseTermDurations", (1..60).toList())
    }

    protected fun loadYearOfConstructions(model: Model) {
        val year = LocalDate.now().year
        model.addAttribute("years", (1900..year).toList())
    }
}
