package com.wutsi.koki.tenant.server.domain

import com.wutsi.koki.refdata.server.domain.JuridictionEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
import java.util.Date

@Entity
@Table(name = "T_BUSINESS")
data class BusinessEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    var companyName: String = "",
    var phone: String? = null,
    var fax: String? = null,
    var email: String? = null,
    var website: String? = null,

    @Column("address_city_fk") var addressCityId: Long? = null,
    @Column("address_state_fk") var addressStateId: Long? = null,
    var addressStreet: String? = null,
    var addressPostalCode: String? = null,
    var addressCountry: String? = null,

    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),

    @BatchSize(20)
    @ManyToMany
    @JoinTable(
        name = "T_BUSINESS_JURIDICTION",
        joinColumns = arrayOf(JoinColumn(name = "business_fk")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "juridiction_fk")),
    )
    var juridictions: MutableList<JuridictionEntity> = mutableListOf(),
)
