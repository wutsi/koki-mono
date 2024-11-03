package com.wutsi.koki.workflow.server.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "T_WI_PARAMETER")
data class ParameterEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "instance_fk")
    val instance: WorkflowInstanceEntity = WorkflowInstanceEntity(),

    val name: String = "",
    var value: String = "",
)
