package com.wutsi.koki.listing.dto
import jakarta.validation.constraints.Min
data class UpdateListingLegalInfoRequest(
    val landTitle: Boolean? = null,
    val technicalFile: Boolean? = null,
    @get:Min(0) val numberOfSigners: Int? = null,
    val mutationType: MutationType? = null,
    val transactionWithNotary: Boolean? = null,
)
