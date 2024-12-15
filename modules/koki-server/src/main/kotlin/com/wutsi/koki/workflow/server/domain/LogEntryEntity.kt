package com.wutsi.koki.workflow.server.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.workflow.dto.LogEntryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_WI_LOG_ENTRY")
data class LogEntryEntity(
    @Id
    val id: String? = null,

    val sequenceNumber: Long = -1,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "workflow_instance_fk")
    val workflowInstanceId: String = "",

    @Column(name = "activity_instance_fk")
    val activityInstanceId: String? = null,

    val type: LogEntryType = LogEntryType.UNKNOWN,
    val message: String = "",
    val exception: String? = null,
    val metadata: String? = null,
    val stackTrace: String? = null,
    val createdAt: Date = Date(),
) {
    @Suppress("UNCHECKED_CAST")
    fun metadataAsMap(objectMapper: ObjectMapper): Map<String, Any> {
        return metadata?.let { objectMapper.readValue(metadata, Map::class.java) as Map<String, Any> }
            ?: emptyMap()
    }
}
