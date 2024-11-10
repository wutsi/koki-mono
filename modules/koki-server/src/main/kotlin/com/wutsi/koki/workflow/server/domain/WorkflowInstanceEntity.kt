package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.workflow.dto.WorkflowStatus
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
import java.util.Date

@Entity
@Table(name = "T_WORKFLOW_INSTANCE")
data class WorkflowInstanceEntity(
    @Id
    val id: String? = null,

    @ManyToOne
    @JoinColumn(name = "tenant_fk")
    val tenant: TenantEntity = TenantEntity(),

    @ManyToOne
    @JoinColumn(name = "workflow_fk")
    val workflow: WorkflowEntity = WorkflowEntity(),

    @BatchSize(20)
    @OneToMany(mappedBy = "instance")
    val activityInstances: MutableList<ActivityInstanceEntity> = mutableListOf(),

    @BatchSize(20)
    @OneToMany(mappedBy = "instance")
    val participants: List<ParticipantEntity> = emptyList(),

    @ManyToOne
    @JoinColumn(name = "approver_fk")
    val approver: UserEntity? = null,

    var status: WorkflowStatus = WorkflowStatus.UNKNOWN,
    var state: String? = null,
    var parameters: String? = null,
    val startAt: Date = Date(),
    val dueAt: Date? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    var startedAt: Date? = null,
    var doneAt: Date? = null
)
