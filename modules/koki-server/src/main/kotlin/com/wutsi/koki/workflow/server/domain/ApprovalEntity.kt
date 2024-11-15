package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.workflow.dto.ApprovalStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_WI_APPROVAL")
data class ApprovalEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "activity_instance_fk")
    val activityInstanceId: String = "",

    @Column(name = "approver_fk")
    val approverId: Long = -1,

    var status: ApprovalStatus = ApprovalStatus.UNKNOWN,
    var comment: String? = null,
    val approvedAt: Date = Date(),
)
