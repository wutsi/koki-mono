package com.wutsi.koki.platform.ai.llm

interface Tool {
    fun function(): LLMFunctionDeclaration
    fun use(args: Map<String, Any>): String
}
