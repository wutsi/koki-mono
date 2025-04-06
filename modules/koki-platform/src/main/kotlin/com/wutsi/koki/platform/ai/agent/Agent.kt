package com.wutsi.koki.platform.ai.agent

import com.wutsi.koki.platform.ai.llm.Document
import java.io.OutputStream
import kotlin.jvm.Throws

interface Agent {
    @Throws(AgentException::class)
    fun run(query: String, file: Document?, output: OutputStream)
}
