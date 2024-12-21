package com.wutsi.koki.workflow.server.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.workflow.dto.WorkflowStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
import java.util.Date

@Entity
@Table(name = "T_WORKFLOW_INSTANCE")
data class WorkflowInstanceEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "approver_fk")
    val approverId: Long? = null,

    @Column(name = "workflow_fk")
    val workflowId: Long = -1,

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    @BatchSize(20)
    @OneToMany
    @JoinColumn(name = "workflow_instance_fk")
    val activityInstances: MutableList<ActivityInstanceEntity> = mutableListOf(),

    @BatchSize(20)
    @OneToMany
    @JoinColumn(name = "workflow_instance_fk")
    val participants: List<ParticipantEntity> = emptyList(),

    var title: String? = null,
    var status: WorkflowStatus = WorkflowStatus.UNKNOWN,
    var state: String? = null,
    var parameters: String? = null,
    val startAt: Date = Date(),
    val dueAt: Date? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var startedAt: Date? = null,
    var doneAt: Date? = null
) {
    @Suppress("UNCHECKED_CAST")
    fun stateAsMap(objectMapper: ObjectMapper): Map<String, Any> {
        return state?.let { objectMapper.readValue(state, Map::class.java) as Map<String, Any> }
            ?: emptyMap()
    }

    @Suppress("UNCHECKED_CAST")
    fun parametersAsMap(objectMapper: ObjectMapper): Map<String, String> {
        return parameters?.let { objectMapper.readValue(parameters, Map::class.java) as Map<String, String> }
            ?: emptyMap()
    }
}
