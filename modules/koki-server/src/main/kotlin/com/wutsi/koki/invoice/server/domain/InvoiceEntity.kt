package com.wutsi.koki.invoice.server.domain

import com.wutsi.koki.invoice.dto.InvoiceStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_INVOICE")
data class InvoiceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val paynowId: String = "",

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "order_fk")
    val orderId: Long? = null,

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "customer_account_fk")
    val customerAccountId: Long? = null,
    val customerName: String = "",
    val customerEmail: String = "",
    val customerPhone: String? = null,
    val customerMobile: String? = null,
    val locale: String? = null,
    var number: Long = -1,
    var status: InvoiceStatus = InvoiceStatus.UNKNOWN,
    val description: String? = null,
    var subTotalAmount: Double = 0.0,
    var totalTaxAmount: Double = 0.0,
    var totalDiscountAmount: Double = 0.0,
    var totalAmount: Double = 0.0,
    var amountPaid: Double = 0.0,
    var amountDue: Double = 0.0,
    val currency: String = "",

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

    @OneToMany(mappedBy = "invoice")
    var items: List<InvoiceItemEntity> = emptyList(),

    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var invoicedAt: Date? = null,
    var dueAt: Date? = null,
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
