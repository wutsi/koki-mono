package com.wutsi.koki.form.server.domain

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_FORM_SUBMISSION")
data class FormSubmissionEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "form_fk")
    val formId: String = "",

    @Column(name = "submitted_by_fk")
    val submittedById: Long? = null,

    val workflowInstanceId: String? = null,
    val activityInstanceId: String? = null,
    var data: String? = null,
    val submittedAt: Date = Date(),
) {
    @Suppress("UNCHECKED_CAST")
    fun dataAsMap(objectMapper: ObjectMapper): Map<String, Any> {
        return if (data == null) {
            return emptyMap()
        } else {
            objectMapper.readValue(data, Map::class.java) as Map<String, Any>
        }
    }
}
