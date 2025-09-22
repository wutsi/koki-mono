package com.wutsi.koki

import com.wutsi.koki.ContactFixtures.contacts
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.ListingFixtures.listings
import com.wutsi.koki.UserFixtures.USER_ID
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.Offer
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.OfferSummary
import com.wutsi.koki.offer.dto.OfferVersion
import com.wutsi.koki.offer.dto.OfferVersionSummary
import com.wutsi.koki.refdata.dto.Money
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object OfferFixtures {
    val offerVersion = OfferVersion(
        id = 33L,
        submittingParty = OfferParty.BUYER,
        price = Money(350000.0, "CAD"),
        status = OfferStatus.SUBMITTED,
        expiresAt = DateUtils.addDays(Date(), 3),
        closingAt = DateUtils.addMonths(Date(), 3),
        createdAt = Date(),
        contingencies = """
                One common example is when one or both parties need to wrap up other real estate deals in order for the transaction to close.
                For instance, a home seller may agree to an offer with the contingency that they must find a new home before they sell.
                If they are unable to find another home within a specified time frame, they may cancel the deal without penalty — so long as this contingency is spelled out in the contract.
                The same can go for the buyer — they might include a contingency that they must sell their previous house before they can buy, for example.
            """.trimIndent()
    )

    val offerVersions = listOf(
        OfferVersionSummary(
            id = 11L,
            submittingParty = OfferParty.BUYER,
            price = Money(350000.0, "CAD"),
            status = OfferStatus.SUBMITTED,
            expiresAt = DateUtils.addDays(Date(), 3),
            closingAt = DateUtils.addMonths(Date(), 3),
            createdAt = Date(),
        ),
        OfferVersionSummary(
            id = 22L,
            submittingParty = OfferParty.BUYER,
            price = Money(300000.0, "CAD"),
            status = OfferStatus.SUBMITTED,
            expiresAt = DateUtils.addDays(Date(), 3),
            closingAt = DateUtils.addMonths(Date(), 3),
            createdAt = Date(),
        ),
        OfferVersionSummary(
            id = 33L,
            submittingParty = OfferParty.BUYER,
            price = Money(550000.0, "CAD"),
            status = OfferStatus.SUBMITTED,
            expiresAt = DateUtils.addDays(Date(), 3),
            closingAt = DateUtils.addMonths(Date(), 3),
            createdAt = Date(),
        ),
    )

    val offer = Offer(
        id = 111L,
        owner = ObjectReference(id = listing.id, type = ObjectType.LISTING),
        sellerAgentUserId = USER_ID,
        buyerContactId = contacts[0].id,
        buyerAgentUserId = USER_ID,
        status = OfferStatus.SUBMITTED,
        totalVersions = 3,
        createdAt = Date(),
        modifiedAt = Date(),
        acceptedAt = DateUtils.addMonths(Date(), 3),
        rejectedAt = DateUtils.addMonths(Date(), 3),
        closedAt = DateUtils.addMonths(Date(), 7),
        version = OfferVersion(
            id = 33L,
            submittingParty = OfferParty.BUYER,
            price = Money(350000.0, "CAD"),
            status = OfferStatus.SUBMITTED,
            expiresAt = DateUtils.addDays(Date(), 3),
            closingAt = DateUtils.addMonths(Date(), 3),
            createdAt = Date(),
            contingencies = """
                One common example is when one or both parties need to wrap up other real estate deals in order for the transaction to close.
                For instance, a home seller may agree to an offer with the contingency that they must find a new home before they sell.
                If they are unable to find another home within a specified time frame, they may cancel the deal without penalty — so long as this contingency is spelled out in the contract.
                The same can go for the buyer — they might include a contingency that they must sell their previous house before they can buy, for example.
            """.trimIndent()
        ),
    )

    val offers = listOf(
        OfferSummary(
            id = 111L,
            owner = ObjectReference(id = listings[0].id, type = ObjectType.LISTING),
            sellerAgentUserId = USER_ID,
            buyerContactId = contacts[0].id,
            buyerAgentUserId = USER_ID,
            status = OfferStatus.SUBMITTED,
            totalVersions = 3,
            createdAt = Date(),
            modifiedAt = Date(),
            versionId = 11L,
        ),
        OfferSummary(
            id = 222L,
            owner = ObjectReference(id = listings[1].id, type = ObjectType.LISTING),
            sellerAgentUserId = USER_ID,
            buyerContactId = contacts[0].id,
            buyerAgentUserId = USER_ID,
            status = OfferStatus.SUBMITTED,
            totalVersions = 3,
            createdAt = Date(),
            modifiedAt = Date(),
            versionId = 22L,
        ),
        OfferSummary(
            id = 333L,
            owner = ObjectReference(id = listings[2].id, type = ObjectType.LISTING),
            sellerAgentUserId = USER_ID,
            buyerContactId = contacts[0].id,
            buyerAgentUserId = USER_ID,
            status = OfferStatus.SUBMITTED,
            totalVersions = 3,
            createdAt = Date(),
            modifiedAt = Date(),
            versionId = 33L,
        ),
    )
}
