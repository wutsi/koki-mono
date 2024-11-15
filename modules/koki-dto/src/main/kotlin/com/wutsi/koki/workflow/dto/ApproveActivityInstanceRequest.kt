package com.wutsi.koki.workflow.dto

data class ApproveActivityInstanceRequest(
    val status: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val comment: String? = null,
)
