package com.wutsi.koki.chatbot.ai.agent

import com.wutsi.koki.platform.ai.llm.LLM

class AgentFactory(
    private val llm: LLM,
) {
    fun createSearchParameterAgent(): SearchParameterAgent {
        return SearchParameterAgent(llm)
    }
}
