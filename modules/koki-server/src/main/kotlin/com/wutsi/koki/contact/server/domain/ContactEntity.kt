package com.wutsi.koki.contact.server.domain

import com.wutsi.koki.contact.dto.Gender
import com.wutsi.koki.form.server.domain.AccountEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_CONTACT")
data class ContactEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "contact_type_fk")
    var contactTypeId: Long? = null,

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deletedById: Long? = null,

    @ManyToOne()
    @JoinColumn(name = "account_fk")
    var account: AccountEntity? = null,

    var salutation: String? = null,
    var firstName: String = "",
    var lastName: String = "",
    var phone: String? = null,
    var mobile: String? = null,
    var email: String? = null,
    var gender: Gender = Gender.UNKNOWN,
    var language: String? = null,
    var profession: String? = null,
    var employer: String? = null,

    var deleted: Boolean = false,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
)
