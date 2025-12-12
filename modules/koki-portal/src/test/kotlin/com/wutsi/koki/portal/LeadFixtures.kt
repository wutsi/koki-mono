package com.wutsi.koki.portal

import com.wutsi.koki.ListingFixtures
import com.wutsi.koki.UserFixtures
import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.lead.dto.LeadMessage
import com.wutsi.koki.lead.dto.LeadMessageSummary
import com.wutsi.koki.lead.dto.LeadSource
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.lead.dto.LeadSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date
import java.util.UUID

object LeadFixtures {
    val LEAD_ID = 111L

    // Lead Fixture
    val message = LeadMessage(
        id = 1L,
        leadId = LEAD_ID,
        content = "I am interested in your listing. Please contact me.",
        createdAt = Date(),
    )
    val messages = listOf(
        LeadMessageSummary(
            id = 1L,
            leadId = LEAD_ID,
            content = "I am interested in your listing. Please contact me.",
            createdAt = Date(),
            visitRequestedAt = DateUtils.addDays(Date(), 7)
        ),
        LeadMessageSummary(
            id = 2L,
            leadId = LEAD_ID,
            content = "I am interested in your listing. Please contact me.",
            createdAt = Date(),
        ),
        LeadMessageSummary(
            id = 3L,
            leadId = LEAD_ID,
            content = "I am interested in your listing. Please contact me.",
            createdAt = Date(),
        ),
    )

    // Lead
    val lead = Lead(
        id = LEAD_ID,
        userId = UserFixtures.user.id,
        deviceId = UUID.randomUUID().toString(),
        lastMessageId = message.id,
        source = LeadSource.LISTING,
        status = LeadStatus.NEW,
        createdAt = Date(),
        modifiedAt = Date(),
        listingId = ListingFixtures.listing.id,
        nextContactAt = DateUtils.addDays(Date(), 7),
        nextVisitAt = DateUtils.addDays(Date(), 14),
    )

    val leads = listOf(
        LeadSummary(
            id = 111L,
            userId = UserFixtures.users[0].id,
            lastMessageId = message.id,
            source = LeadSource.LISTING,
            status = LeadStatus.NEW,
            createdAt = Date(),
            modifiedAt = Date(),
            listingId = ListingFixtures.listing.id,
            nextContactAt = DateUtils.addDays(Date(), 7),
        ),
        LeadSummary(
            id = 112L,
            userId = UserFixtures.users[0].id,
            lastMessageId = message.id,
            source = LeadSource.SOCIAL_MEDIA,
            status = LeadStatus.CONTACT_LATER,
            createdAt = Date(),
            modifiedAt = Date(),
            listingId = ListingFixtures.listings[0].id,
            nextContactAt = DateUtils.addDays(Date(), 7),
            nextVisitAt = DateUtils.addDays(Date(), 14),
        ),
        LeadSummary(
            id = 113L,
            userId = UserFixtures.users[0].id,
            lastMessageId = message.id,
            source = LeadSource.AGENT,
            status = LeadStatus.VISIT_SET,
            createdAt = Date(),
            modifiedAt = Date(),
            listingId = ListingFixtures.listings[0].id,
            nextContactAt = DateUtils.addDays(Date(), 7),
            nextVisitAt = DateUtils.addDays(Date(), 7),
        ),
        LeadSummary(
            id = 114L,
            userId = UserFixtures.users[0].id,
            lastMessageId = message.id,
            source = LeadSource.AGENT,
            status = LeadStatus.CONTACTED,
            createdAt = Date(),
            modifiedAt = Date(),
            listingId = ListingFixtures.listings[0].id,
            nextContactAt = DateUtils.addDays(Date(), 7),
        ),
    )
}
