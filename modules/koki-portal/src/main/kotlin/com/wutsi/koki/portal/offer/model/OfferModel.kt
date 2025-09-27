package com.wutsi.koki.portal.offer.model

import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.common.model.ObjectReferenceModel
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class OfferModel(
    val id: Long = -1,
    val version: OfferVersionModel = OfferVersionModel(),
    val owner: ObjectReferenceModel? = null,
    val buyerContact: ContactModel = ContactModel(),
    val buyerAgentUser: UserModel = UserModel(),
    val sellerAgentUser: UserModel = UserModel(),
    val listing: ListingModel? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val closedAt: Date? = null,
    val closedAtText: String? = null,
    val acceptedAt: Date? = null,
    val acceptedAtText: String? = null,
    val rejectedAt: Date? = null,
    val rejectedAtText: String? = null,
    val totalVersions: Int = -1,
    val status: OfferStatus = OfferStatus.UNKNOWN,
    val readOnly: Boolean = false,
) {
    val name: String
        get() = buyerAgentUser.displayName ?: buyerContact.name

    val statusClosed: Boolean
        get() = status == OfferStatus.CLOSED

    val statusSubmitted: Boolean
        get() = status == OfferStatus.SUBMITTED

    val statusAccepted: Boolean
        get() = status == OfferStatus.ACCEPTED

    val statusRejected: Boolean
        get() = status == OfferStatus.REJECTED

    /**
     * - User can access AND user is the seller or byuer agent
     * - user has full access
     */
    fun canAccess(user: UserModel?): Boolean {
        return ((user?.canAccess("offer") == true) && (user.id == sellerAgentUser?.id || user.id == buyerAgentUser?.id)) ||
            (user?.hasFullAccess("offer") == true)
    }

    /**
     * - ...
     */
    fun canManage(user: UserModel?): Boolean {
        return (user?.canManage("offer") == true) && (user.id == sellerAgentUser.id)
    }

    fun canAcceptOrRejectOrCounter(user: UserModel?): Boolean {
        return (user?.canManage("offer") == true) &&
            (status == OfferStatus.SUBMITTED) &&
            (user.id == version.assigneeUserId)
    }

    fun canCloseOrCancel(user: UserModel?): Boolean {
        return canManage(user) && (status == OfferStatus.ACCEPTED)
    }

    fun canWithdraw(user: UserModel?): Boolean {
        return (user?.canManage("offer") == true) &&
            (status == OfferStatus.SUBMITTED) &&
            (user.id == buyerAgentUser.id &&
                version.submittingParty == OfferParty.BUYER ||
                user.id == sellerAgentUser.id &&
                version.submittingParty == OfferParty.SELLER)
    }
}
