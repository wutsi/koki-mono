package com.wutsi.koki.form.server.domain

import com.wutsi.koki.account.dto.Sex
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
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

    @Column(name = "account_fk")
    val accountId: String = "",

    var firstName: String = "",
    var lastName: String = "",
    var phone: String? = null,
    var mobile: String? = null,
    var email: String? = null,
    var title: String? = null,
    var sex: Sex = Sex.UNKNOWN,

    var deleted: Boolean = false,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
)
