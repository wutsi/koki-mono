package com.wutsi.koki.product.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class ServiceDetailsData(
    val quantity: Int? = null,

    @Column(name = "unit_fk")
    val unitId: Long? = null,
)
