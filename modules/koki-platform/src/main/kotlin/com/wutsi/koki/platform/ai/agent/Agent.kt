package com.wutsi.koki.platform.ai.agent

import java.io.File
import java.io.OutputStream
import kotlin.jvm.Throws

interface Agent {
    @Throws(AgentException::class)
    fun run(query: String, file: File?, output: OutputStream)
}
