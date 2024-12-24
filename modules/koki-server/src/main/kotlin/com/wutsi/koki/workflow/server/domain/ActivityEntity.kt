package com.wutsi.koki.workflow.server.domain

import com.fasterxml.jackson.databind.ObjectMapper
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

    @Column(name = "script_fk")
    var scriptId: String? = null,

    @Column(name = "service_fk")
    var serviceId: String? = null,

    var name: String = "",
    var title: String? = null,
    var description: String? = null,
    var active: Boolean = true,
    var type: ActivityType = ActivityType.UNKNOWN,
    var requiresApproval: Boolean = false,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var event: String? = null,
    var path: String? = null,
    var method: String? = null,
    var input: String? = null,
    var output: String? = null,
    var recipientEmail: String? = null,
    var recipientDisplayName: String? = null,
) {
    fun inputAsMap(objectMapper: ObjectMapper): Map<String, Any> {
        return toMap(input, objectMapper)
    }

    fun outputAsMap(objectMapper: ObjectMapper): Map<String, Any> {
        return toMap(output, objectMapper)
    }

    @Suppress("UNCHECKED_CAST")
    private fun toMap(value: String?, objectMapper: ObjectMapper): Map<String, Any> {
        return value?.let { objectMapper.readValue(value, Map::class.java) as Map<String, Any> }
            ?: emptyMap()
    }
}
