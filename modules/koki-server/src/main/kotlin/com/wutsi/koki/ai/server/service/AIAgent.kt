package com.wutsi.koki.ai.server.service

interface AIAgent {
    fun notify(event: Any): Boolean
}
