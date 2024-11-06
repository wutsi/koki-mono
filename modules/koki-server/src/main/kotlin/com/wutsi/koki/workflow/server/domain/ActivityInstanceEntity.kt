package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.WorkflowStatus
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_WI_ACTIVITY")
data class ActivityInstanceEntity(
    @Id
    val id: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_fk")
    val activity: ActivityEntity = ActivityEntity(),

    @ManyToOne
    @JoinColumn(name = "instance_fk")
    val instance: WorkflowInstanceEntity = WorkflowInstanceEntity(),

    @ManyToOne
    @JoinColumn(name = "assignee_fk")
    var assignee: UserEntity? = null,

    @ManyToOne
    @JoinColumn(name = "approver_fk")
    var approver: UserEntity? = null,

    var status: WorkflowStatus = WorkflowStatus.UNKNOWN,
    var approval: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val createdAt: Date = Date(),
    var approvedAt: Date? = null,
    var startedAt: Date? = null,
    var doneAt: Date? = null
)
