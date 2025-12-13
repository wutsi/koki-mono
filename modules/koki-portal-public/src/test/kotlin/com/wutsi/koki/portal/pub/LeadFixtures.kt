package com.wutsi.koki.portal.pub

import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.lead.dto.LeadSummary

object LeadFixtures {
    val lead = Lead(
        userId = UserFixtures.USER_ID,
    )

    val leads = listOf(
        LeadSummary(
            userId = UserFixtures.USER_ID,
        )
    )
}
