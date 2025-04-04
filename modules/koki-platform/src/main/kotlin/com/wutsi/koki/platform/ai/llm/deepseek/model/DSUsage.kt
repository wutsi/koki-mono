package com.wutsi.koki.platform.ai.llm.deepseek.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DSUsage(
    @get:JsonProperty("completion_tokens")
    val completionTokens: Int = -1,

    @get:JsonProperty("prompt_tokens")
    val promptTokens: Int = -1,

    @get:JsonProperty("prompt_cache_hit_tokens")
    val promptCacheHitTokens: Int = -1,

    @get:JsonProperty("prompt_cache_miss_tokens")
    val promptCacheMissTokens: Int = -1,

    @get:JsonProperty("total_tokens")
    val totalTokens: Int = -1,
)
