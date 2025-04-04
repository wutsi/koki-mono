package com.wutsi.koki.platform.ai.agent

import java.io.OutputStream
import kotlin.jvm.Throws

interface Agent {
    @Throws(AgentException::class)
    fun run(query: String, output: OutputStream)
}
