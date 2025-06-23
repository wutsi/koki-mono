package com.wutsi.koki.chatbot.ai.agent

import com.wutsi.koki.chatbot.ai.tool.SearchRoomTool
import com.wutsi.koki.platform.ai.llm.LLM

class AgentFactory(
    private val llm: LLM,
    private val searchRoomTool: SearchRoomTool,
) {
    fun crateSearchAgent(): SearchAgent {
        return SearchAgent(
            llm = llm,
            searchRoomTool = searchRoomTool,
        )
    }
}
