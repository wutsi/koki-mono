package com.wutsi.koki.tenant.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_ATTRIBUTE")
data class AttributeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    var name: String = "",
    var label: String? = null,
    var description: String? = null,
    var choices: String? = null,
    var type: AttributeType = AttributeType.TEXT,
    var active: Boolean = true,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
) {
    companion object {
        const val CSV_HEADER_NAME = "name"
        const val CSV_HEADER_TYPE = "type"
        const val CSV_HEADER_ACTIVE = "active"
        const val CSV_HEADER_CHOICES = "choices"
        const val CSV_HEADER_LABEL = "label"
        const val CSV_HEADER_DESCRIPTION = "description"

        val CSV_HEADERS = listOf(
            CSV_HEADER_NAME,
            CSV_HEADER_TYPE,
            CSV_HEADER_ACTIVE,
            CSV_HEADER_CHOICES,
            CSV_HEADER_LABEL,
            CSV_HEADER_DESCRIPTION,
        )
    }
}
