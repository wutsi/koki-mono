package com.wutsi.koki.portal.pub

import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.lead.dto.LeadSummary

object LeadFixtures {
    val lead = Lead(
        userId = UserFixtures.USER_ID,
        firstName = "Ray",
        lastName = "Sponsible",
        email = "ray.sponsible@gmail.com",
        phoneNumber = "+15147580000",
    )

    val leads = listOf(
        LeadSummary(
            userId = UserFixtures.USER_ID,
            firstName = "Ray",
            lastName = "Sponsible",
            email = "ray.sponsible@gmail.com",
            phoneNumber = "+15147580000",
        )
    )
}
