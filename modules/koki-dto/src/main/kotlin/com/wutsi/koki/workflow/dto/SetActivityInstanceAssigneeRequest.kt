package com.wutsi.koki.workflow.dto

import jakarta.validation.constraints.NotEmpty

data class SetActivityInstanceAssigneeRequest(
    @get:NotEmpty val activityInstanceIds: List<String> = emptyList(),
    val userId: Long = -1,
)
