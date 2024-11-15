package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.workflow.dto.WorkflowStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
import java.util.Date

@Entity
@Table(name = "T_WORKFLOW_INSTANCE")
data class WorkflowInstanceEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "approver_fk")
    val approverId: Long? = null,

    @Column(name = "workflow_fk")
    val workflowId: Long = -1,

    @BatchSize(20)
    @OneToMany(mappedBy = "workflowInstanceId")
    val activityInstances: MutableList<ActivityInstanceEntity> = mutableListOf(),

    @BatchSize(20)
    @OneToMany
    @JoinColumn(name = "workflow_instance_fk")
    val participants: List<ParticipantEntity> = emptyList(),

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
