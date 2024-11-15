package com.wutsi.koki.form.server.domain

import com.wutsi.koki.form.dto.FormDataStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_FORM_DATA")
data class FormDataEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "form_fk")
    val formId: String = "",

    val workflowInstanceId: String? = null,
    val status: FormDataStatus = FormDataStatus.UNKNOWN,
    var data: String? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date()
)
