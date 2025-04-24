package com.wutsi.koki.portal.client

import com.wutsi.koki.portal.client.AccountFixtures.accounts
import com.wutsi.koki.portal.client.UserFixtures.users
import com.wutsi.koki.tax.dto.Tax
import com.wutsi.koki.tax.dto.TaxStatus
import com.wutsi.koki.tax.dto.TaxSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object TaxFixtures {
    // Taxes
    val taxes = listOf(
        TaxSummary(
            id = 100,
            accountId = accounts[0].id,
            accountantId = users[0].id,
            technicianId = users[1].id,
            assigneeId = users[0].id,
            taxTypeId = TenantFixtures.types[0].id,
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
            taxTypeId = TenantFixtures.types[1].id,
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
            taxTypeId = TenantFixtures.types[2].id,
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
        taxTypeId = TenantFixtures.types[0].id,
        fiscalYear = 2022,
        status = TaxStatus.GATHERING_DOCUMENTS,
        startAt = DateUtils.addYears(Date(), -2),
        modifiedById = users[0].id,
        createdById = users[0].id,
        description = "This is the description of the Tax Report",
        totalRevenue = 600.0,
        totalLaborCost = 350.0,
        totalLaborDuration = 25,
        currency = "CAD",
        productCount = 3
    )
}
