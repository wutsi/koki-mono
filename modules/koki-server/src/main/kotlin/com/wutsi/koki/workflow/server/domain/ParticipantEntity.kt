package com.wutsi.koki.workflow.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_WI_PARTICIPANT")
data class ParticipantEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "workflow_instance_fk")
    val workflowInstanceId: String = "",

    @Column(name = "user_fk")
    val userId: Long = -1,

    @Column(name = "role_fk")
    val roleId: Long = -1,
)
