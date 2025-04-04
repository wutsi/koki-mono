package com.wutsi.koki.platform.ai.llm.deepseek.model

import com.fasterxml.jackson.annotation.JsonProperty

class DSCompletionResponse(
    val id: String = "",
    val model: String = "",

    @get:JsonProperty("system_fingerprint")
    val systemFingerprint: String = "",

    val created: Long = -1,
    val choices: List<DSChoice> = emptyList(),
    val `object`: String = "",
    val usage: DSUsage? = null
)
