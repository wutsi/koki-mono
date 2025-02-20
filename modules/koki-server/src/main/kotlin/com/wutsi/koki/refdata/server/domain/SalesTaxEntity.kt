package com.wutsi.koki.refdata.server.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "T_SALES_TAX")
data class SalesTaxEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "juridiction_fk")
    val juridiction: JuridictionEntity = JuridictionEntity(),

    val name: String = "",
    var rate: Double = 0.0,
    var priority: Int = 0,
    var active: Boolean = true,
)
