package com.wutsi.koki.tenant.server.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_ROLE")
data class RoleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "tenant_fk")
    val tenant: TenantEntity = TenantEntity(),

    var name: String = "",
    var active: Boolean = true,
    var description: String? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
) {
    companion object {
        const val CSV_HEADER_NAME = "name"
        const val CSV_HEADER_ACTIVE = "active"
        const val CSV_HEADER_DESCRIPTION = "description"

        val CSV_HEADERS = listOf(
            CSV_HEADER_NAME,
            CSV_HEADER_ACTIVE,
            CSV_HEADER_DESCRIPTION,
        )
    }
}
