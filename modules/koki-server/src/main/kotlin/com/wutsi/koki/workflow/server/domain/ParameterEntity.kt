package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.tenant.server.domain.RoleEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "T_WI_PARTICIPANT")
data class ParticipantEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "instance_fk")
    val instance: WorkflowInstanceEntity = WorkflowInstanceEntity(),

    @ManyToOne
    @JoinColumn(name = "user_fk")
    val user: UserEntity = UserEntity(),

    @ManyToOne
    @JoinColumn(name = "role_fk")
    val role: RoleEntity = RoleEntity(),
)
