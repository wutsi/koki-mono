package com.wutsi.koki

import com.wutsi.koki.AccountFixtures.accounts
import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.tax.dto.Tax
import com.wutsi.koki.tax.dto.TaxStatus
import com.wutsi.koki.tax.dto.TaxSummary
import com.wutsi.koki.tax.dto.TaxType
import com.wutsi.koki.tax.dto.TaxTypeSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object TaxFixtures {
    // Tax Types
    val taxTypes = listOf(
        TaxTypeSummary(
            id = 100,
            name = "T1",
            title = "Personal Taxes"
        ),
        TaxTypeSummary(
            id = 101,
            name = "Corporate Taxes",
        ),
        TaxTypeSummary(
            id = 102,
            name = "TVQ",
        ),
    )

    val taxType = TaxType(
        id = 100,
        name = "T1",
        description = "Tax for personal user",
    )

    // Taxs
    val NEW_TAX_ID = 5555L
    val taxes = listOf(
        TaxSummary(
            id = 100,
            accountId = accounts[0].id,
            accountantId = users[0].id,
            technicianId = users[1].id,
            assigneeId = users[0].id,
            taxTypeId = taxTypes[0].id,
            fiscalYear = 2024,
            status = TaxStatus.PROCESSING,
            startAt = Date(),
            dueAt = DateUtils.addDays(Date(), 10),
            modifiedById = users[0].id,
            createdById = users[0].id,
        ),
        TaxSummary(
            id = 101,
            accountId = accounts[0].id,
            accountantId = users[0].id,
            taxTypeId = taxTypes[0].id,
            fiscalYear = 2023,
            status = TaxStatus.DONE,
            startAt = DateUtils.addYears(Date(), -1),
            modifiedById = users[0].id,
            createdById = users[0].id,
        ),
        TaxSummary(
            id = 102,
            accountId = accounts[0].id,
            accountantId = users[0].id,
            taxTypeId = taxTypes[0].id,
            fiscalYear = 2022,
            status = TaxStatus.DONE,
            startAt = DateUtils.addYears(Date(), -2),
            modifiedById = users[0].id,
            createdById = users[0].id,
        ),
    )

    val tax = Tax(
        id = 100,
        accountId = accounts[0].id,
        accountantId = users[0].id,
        technicianId = users[1].id,
        assigneeId = users[0].id,
        taxTypeId = taxTypes[0].id,
        fiscalYear = 2022,
        status = TaxStatus.PREPARING,
        startAt = DateUtils.addYears(Date(), -2),
        modifiedById = users[0].id,
        createdById = users[0].id,
        description = "This is the description of the Tax Report",
    )
}
