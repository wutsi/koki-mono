package com.wutsi.koki.workflow.dto

import jakarta.validation.constraints.NotEmpty

data class SetActivityInstanceApproverRequest(
    @get:NotEmpty val activityInstanceIds: List<String> = emptyList(),
    val userId: Long = -1,
)
