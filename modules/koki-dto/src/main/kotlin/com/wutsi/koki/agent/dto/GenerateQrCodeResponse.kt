package com.wutsi.koki.agent.dto

data class GenerateQrCodeResponse(
    val agentId: Long = -1,
    val qrCodeUrl: String = ""
)
