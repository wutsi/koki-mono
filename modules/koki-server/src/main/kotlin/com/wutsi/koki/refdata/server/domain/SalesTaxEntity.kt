package com.wutsi.koki.refdata.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_SALES_TAX")
data class SalesTaxEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "state_fk")
    val stateId: Long? = null,

    val country: String = "",
    val name: String = "",
    var rate: Double = 0.0,
    var priority: Int = 0,
    var active: Boolean = true,
)
