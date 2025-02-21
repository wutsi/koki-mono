package com.wutsi.koki.tenant.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_BUSINESS_TAX_IDENTIFIER")
data class BusinessTaxIdentifierEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "sales_tax_fk")
    val salesTaxId: Long = -1,

    @Column(name = "business_fk")
    val businessId: Long = -1,

    var number: String = "",
)
