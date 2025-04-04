package com.wutsi.koki.platform.ai.agent

import com.wutsi.koki.platform.ai.llm.FunctionDeclaration

interface Tool {
    fun function(): FunctionDeclaration
    fun use(args: Map<String, Any>): String
}
