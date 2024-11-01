package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.workflow.dto.ActivityType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_ACTIVITY")
data class ActivityEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "workflow_fk")
    val workflow: WorkflowEntity = WorkflowEntity(),

    @ManyToMany
    @JoinTable(
        name = "T_ACTIVITY_PREDECESSOR",
        joinColumns = arrayOf(JoinColumn(name = "activity_fk")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "predecessor_fk")),
    )
    val predecessors: MutableList<ActivityEntity> = mutableListOf(),

    val code: String = "",
    var name: String = "",
    var description: String? = null,
    var active: Boolean = true,
    var type: ActivityType = ActivityType.UNKNOWN,
    var requiresApproval: Boolean = true,
    var tags: String? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
