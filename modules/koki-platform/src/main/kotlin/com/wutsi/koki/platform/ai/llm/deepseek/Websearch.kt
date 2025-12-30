package com.wutsi.koki.platform.ai.llm.deepseek

/**
 * Performs the search on the internet and result list is result with title, link and snippet.
 */
interface Websearch {
    fun search(query: String): String
}
