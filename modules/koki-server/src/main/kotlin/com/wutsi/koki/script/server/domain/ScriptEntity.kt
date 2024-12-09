package com.wutsi.koki.script.server.domain

import com.wutsi.koki.script.dto.Language
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_SCRIPT")
data class ScriptEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    var name: String = "",
    var title: String? = null,
    var description: String? = null,
    var language: Language = Language.UNKNOWN,
    var code: String = "",
    var parameters: String? = null,
    var active: Boolean = true,
    var deleted: Boolean = false,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
) {
    fun parameterAsList(): List<String> {
        return parameters?.let { params -> params.split(",").map { it.trim() } } ?: emptyList()
    }
}
