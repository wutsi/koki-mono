package com.wutsi.koki.portal.offer.model

import com.wutsi.koki.offer.dto.OfferParty
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
    val buyerAgentUser: UserModel? = null,
    val sellerContact: ContactModel = ContactModel(),
    val sellerAgentUser: UserModel? = null,
    val listing: ListingModel? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val totalVersions: Int = -1,
    val readOnly: Boolean = false
) {
    val name: String
        get() = buyerAgentUser?.displayName ?: buyerContact.name

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
        return user?.canManage("offer") == true &&
            (
                (user.id == sellerAgentUser?.id && version.submittingParty == OfferParty.BUYER) ||
                    (user.id == buyerAgentUser?.id && version.submittingParty == OfferParty.SELLER)
                )
    }
}
