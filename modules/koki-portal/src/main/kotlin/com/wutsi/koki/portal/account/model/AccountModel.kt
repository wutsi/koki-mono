package com.wutsi.koki.portal.account.model

import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.tenant.model.TypeModel
import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class AccountModel(
    val id: Long = -1,
    val accountType: TypeModel? = null,
    val name: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val email: String = "",
    val website: String? = null,
    val language: String? = null,
    val languageText: String? = null,
    val description: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val createdBy: UserModel? = null,
    val modifiedBy: UserModel? = null,
    val managedBy: UserModel? = null,
    val attributes: List<AccountAttributeModel> = emptyList(),
    val shippingAddress: AddressModel? = null,
    val billingAddress: AddressModel? = null,
    val billingSameAsShippingAddress: Boolean = true,
    val readOnly: Boolean = false,
    val user: UserModel? = null,
    val invitation: InvitationModel? = null,
)
