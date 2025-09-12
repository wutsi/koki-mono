package com.wutsi.koki.portal.offer.page.model

import com.wutsi.koki.portal.common.model.ObjectReferenceModel
import com.wutsi.koki.portal.contact.model.ContactModel
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
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
