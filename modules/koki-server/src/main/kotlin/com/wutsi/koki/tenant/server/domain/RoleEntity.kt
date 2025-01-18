package com.wutsi.koki.tenant.server.domain

import com.wutsi.koki.module.server.domain.PermissionEntity
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
@Table(name = "T_ROLE")
data class RoleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    var name: String = "",
    var title: String? = null,
    var active: Boolean = true,
    var description: String? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),

    @BatchSize(20)
    @ManyToMany
    @JoinTable(
        name = "T_ROLE_PERMISSION",
        joinColumns = arrayOf(JoinColumn(name = "role_fk")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "permission_fk")),
    )
    val permissions: MutableList<PermissionEntity> = mutableListOf(),
) {
    companion object {
        const val CSV_HEADER_NAME = "name"
        const val CSV_HEADER_TITLE = "title"
        const val CSV_HEADER_ACTIVE = "active"
        const val CSV_HEADER_DESCRIPTION = "description"

        val CSV_HEADERS = listOf(
            CSV_HEADER_NAME,
            CSV_HEADER_TITLE,
            CSV_HEADER_ACTIVE,
            CSV_HEADER_DESCRIPTION,
        )
    }
}
