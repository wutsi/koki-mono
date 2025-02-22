package com.wutsi.koki.tenant.dto

import com.wutsi.koki.refdata.dto.Address
import java.util.Date

data class Business(
    val id: Long = -1,
    val companyName: String = "",
    val phone: String? = null,
    val fax: String? = null,
    val email: String? = null,
    val website: String? = null,
    val address: Address? = null,
    val juridictionIds: List<Long> = emptyList(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdById: Long? = null,
    val modifiedById: Long? = null,
)
