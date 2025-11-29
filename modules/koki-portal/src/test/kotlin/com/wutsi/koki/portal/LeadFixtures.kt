package com.wutsi.koki.portal

import com.wutsi.koki.ListingFixtures
import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.lead.dto.LeadSource
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.lead.dto.LeadSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date
import java.util.UUID

object LeadFixtures {
    val lead = Lead(
        id = 111,
        firstName = "Ray",
        lastName = "Sponsible",
        email = "ray.sponsible@gmail.com",
        phoneNumber = "+15147580000",
        deviceId = UUID.randomUUID().toString(),
        source = LeadSource.WEBSITE,
        status = LeadStatus.NEW,
        createdAt = Date(),
        modifiedAt = Date(),
        listingId = ListingFixtures.listing.id,
        message = "I am interested in your listing. Please contact me.",
        nextContactAt = DateUtils.addDays(Date(), 7),
        nextVisitAt = DateUtils.addDays(Date(), 14),
    )

    val leads = listOf(
        LeadSummary(
            firstName = "Ray",
            lastName = "Sponsible",
            email = "ray.sponsible@gmail.com",
            phoneNumber = "+15147580000",
            source = LeadSource.WEBSITE,
            status = LeadStatus.NEW,
            createdAt = Date(),
            modifiedAt = Date(),
            listingId = ListingFixtures.listing.id,
            nextContactAt = DateUtils.addDays(Date(), 7),
        ),
        LeadSummary(
            firstName = "John",
            lastName = "Smith",
            email = "john.smith@gmail.com",
            phoneNumber = "+15147580011",
            source = LeadSource.SOCIAL_MEDIA,
            status = LeadStatus.NEW,
            createdAt = Date(),
            modifiedAt = Date(),
            listingId = ListingFixtures.listings[0].id,
            nextContactAt = DateUtils.addDays(Date(), 7),
            nextVisitAt = DateUtils.addDays(Date(), 14),
        ),
        LeadSummary(
            firstName = "Thomas",
            lastName = "Nkono",
            email = "thomas.nkono@gmail.com",
            phoneNumber = "+15147580022",
            source = LeadSource.REFERRAL,
            status = LeadStatus.CONTACTED,
            createdAt = Date(),
            modifiedAt = Date(),
            listingId = ListingFixtures.listings[0].id,
            nextContactAt = DateUtils.addDays(Date(), 7),
            nextVisitAt = DateUtils.addDays(Date(), 7),
        ),
        LeadSummary(
            firstName = "Yo",
            lastName = "Man",
            email = "yo.man@gmail.com",
            phoneNumber = "+15147580033",
            source = LeadSource.REFERRAL,
            status = LeadStatus.CONTACTED,
            createdAt = Date(),
            modifiedAt = Date(),
            listingId = ListingFixtures.listings[0].id,
            nextContactAt = DateUtils.addDays(Date(), 7),
        ),
    )
}
