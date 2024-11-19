package com.wutsi.koki.workflow.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
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

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "approver_role_fk")
    var approverRoleId: Long? = null,

    @BatchSize(20)
    @OneToMany(mappedBy = "workflow")
    val activities: List<ActivityEntity> = emptyList(),

    @BatchSize(20)
    @OneToMany(mappedBy = "workflowId")
    val flows: List<FlowEntity> = emptyList(),

    var name: String = "",
    var title: String? = null,
    var description: String? = null,
    var active: Boolean = true,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var parameters: String? = null,
    var workflowInstanceCount: Long = 0,
) {
    fun parameterAsList(): List<String> {
        return parameters?.let { params -> params.split(",").map { it.trim() } } ?: emptyList()
    }
}
