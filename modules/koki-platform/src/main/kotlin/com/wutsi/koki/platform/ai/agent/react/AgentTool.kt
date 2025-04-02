package com.wutsi.koki.platform.ai.agent.react

import com.wutsi.koki.platform.ai.llm.FunctionDeclaration

interface AgentTool {
    fun function(): FunctionDeclaration
    fun use(args: Map<String, Any>): String
}
