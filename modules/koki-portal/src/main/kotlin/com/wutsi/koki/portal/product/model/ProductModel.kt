package com.wutsi.koki.portal.product.model

import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.product.dto.ProductType
import java.util.Date

data class ProductModel(
    val id: Long = -1,
    val type: ProductType = ProductType.UNKNOWN,
    val name: String = "",
    val code: String? = null,
    val description: String? = null,
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val createdBy: UserModel? = null,
    val modifiedBy: UserModel? = null,
    val serviceDetails: ServiceDetailsModel? = null
)
