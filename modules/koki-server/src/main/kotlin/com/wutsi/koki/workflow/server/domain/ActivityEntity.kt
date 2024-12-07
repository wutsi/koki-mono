package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.workflow.dto.ActivityType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_ACTIVITY")
data class ActivityEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "workflow_fk")
    val workflowId: Long = -1,

    @Column(name = "role_fk")
    var roleId: Long? = null,

    @Column(name = "form_fk")
    var formId: String? = null,

    @Column(name = "message_fk")
    var messageId: String? = null,

    var name: String = "",
    var title: String? = null,
    var description: String? = null,
    var active: Boolean = true,
    var type: ActivityType = ActivityType.UNKNOWN,
    var requiresApproval: Boolean = false,
    var tags: String? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
