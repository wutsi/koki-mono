package com.wutsi.koki.platform.ai.llm.kimi

import com.wutsi.koki.platform.ai.llm.LLMFunctionDeclaration
import com.wutsi.koki.platform.ai.llm.Tool
import tools.jackson.databind.json.JsonMapper

class KimiTool(private val func: LLMFunctionDeclaration) : Tool {
    private val jsonMapper = JsonMapper()

    override fun function(): LLMFunctionDeclaration = func

    override fun use(args: Map<String, Any>): String {
        return jsonMapper.writeValueAsString(args)
    }
}
