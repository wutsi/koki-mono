package com.wutsi.koki.workflow.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "T_FLOW")
data class FlowEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "workflow_fk")
    val workflowId: Long = -1,

    @ManyToOne
    @JoinColumn(name = "from_fk")
    val from: ActivityEntity = ActivityEntity(),

    @ManyToOne
    @JoinColumn(name = "to_fk")
    val to: ActivityEntity = ActivityEntity(),

    var expression: String? = null,
)
