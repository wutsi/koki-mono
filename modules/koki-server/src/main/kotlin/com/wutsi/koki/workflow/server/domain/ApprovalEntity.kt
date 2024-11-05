package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.workflow.dto.ApprovalStatus
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_WI_APPROVAL")
data class ApprovalEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "activity_instance_fk")
    val activityInstance: ActivityInstanceEntity = ActivityInstanceEntity(),

    @ManyToOne
    @JoinColumn(name = "approver_fk")
    val approver: UserEntity = UserEntity(),

    var status: ApprovalStatus = ApprovalStatus.UNKNOWN,
    var comment: String? = null,
    val approvedAt: Date = Date(),
)
