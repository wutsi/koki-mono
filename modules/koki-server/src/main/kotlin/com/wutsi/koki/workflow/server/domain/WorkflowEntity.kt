package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.tenant.server.domain.TenantEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
import java.util.Date

@Entity
@Table(name = "T_WORKFLOW")
data class WorkflowEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "tenant_fk")
    val tenant: TenantEntity = TenantEntity(),

    @BatchSize(20)
    @OneToMany(mappedBy = "workflow")
    val activities: List<ActivityEntity> = emptyList(),

    var name: String = "",
    var title: String? = null,
    var description: String? = null,
    var active: Boolean = true,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var parameters: String? = null,
) {
    fun parameterAsList(): List<String> {
        return parameters?.let { params -> params.split(",").map { it.trim() } } ?: emptyList()
    }
}
