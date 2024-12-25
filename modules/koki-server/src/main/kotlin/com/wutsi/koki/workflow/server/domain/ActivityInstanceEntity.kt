package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.WorkflowStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_WI_ACTIVITY")
data class ActivityInstanceEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "activity_fk")
    val activityId: Long = -1,

    @Column(name = "workflow_instance_fk")
    val workflowInstanceId: String = "",

    @Column(name = "assignee_fk")
    var assigneeId: Long? = null,

    @Column(name = "approver_fk")
    var approverId: Long? = null,

    var status: WorkflowStatus = WorkflowStatus.UNKNOWN,
    var approval: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var approvedAt: Date? = null,
    var startedAt: Date? = null,
    var doneAt: Date? = null
)
