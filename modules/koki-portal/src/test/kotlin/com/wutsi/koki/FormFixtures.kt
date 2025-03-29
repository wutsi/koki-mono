package com.wutsi.koki

import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object FormFixtures {
    val forms = listOf(
        FormSummary(
            id = 100,
            name = "T1 - Personal Taxes Control List",
            active = true,
        ),
        FormSummary(
            id = 101,
            name = "T2 - Corporate Taxes Control List",
            active = true,
        ),
        FormSummary(
            id = 103,
            name = "GHS/PST - GHS/PST Control List",
            active = false,
        ),
    )

    val form = Form(
        id = 100,
        name = "T1 - Personal Taxes Control List",
        description = "Form for client to fill in before we process their taxes",
        createdAt = DateUtils.addDays(Date(), -30),
        createdById = users[0].id,
        modifiedAt = DateUtils.addDays(Date(), -7),
        modifiedById = users[1].id,
    )
}
