package com.wutsi.koki.form.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_ACCOUNT")
data class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "account_type_fk")
    var accountTypeId: Long? = null,

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deletedById: Long? = null,

    @Column(name = "managed_by_fk")
    var managedById: Long? = null,

    @OneToMany()
    @JoinColumn("account_fk")
    var accountAttributes: List<AccountAttributeEntity> = emptyList(),

    var name: String = "",
    var phone: String? = null,
    var mobile: String? = null,
    var email: String? = null,
    var website: String? = null,
    var language: String? = null,
    var description: String? = null,

    @Column("shipping_city_fk") var shippingCityId: Long? = null,
    @Column("shipping_state_fk") var shippingStateId: Long? = null,
    var shippingStreet: String? = null,
    var shippingPostalCode: String? = null,
    var shippingCountry: String? = null,

    @Column("billing_city_fk") var billingCityId: Long? = null,
    @Column("billing_state_fk") var billingStateId: Long? = null,
    var billingStreet: String? = null,
    var billingPostalCode: String? = null,
    var billingCountry: String? = null,

    var deleted: Boolean = false,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
) {
    fun hasShippingAddress(): Boolean {
        return shippingCityId != null ||
            shippingStateId != null ||
            !shippingPostalCode.isNullOrEmpty() ||
            !shippingStreet.isNullOrEmpty() ||
            !shippingCountry.isNullOrEmpty()
    }

    fun hasBillingAddress(): Boolean {
        return billingCityId != null ||
            billingStateId != null ||
            !billingPostalCode.isNullOrEmpty() ||
            !billingStreet.isNullOrEmpty() ||
            !billingCountry.isNullOrEmpty()
    }
}
