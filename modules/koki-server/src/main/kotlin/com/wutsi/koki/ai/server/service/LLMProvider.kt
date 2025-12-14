package com.wutsi.koki.ai.server.service

import com.wutsi.koki.config.AIConfiguration
import com.wutsi.koki.platform.ai.llm.LLM
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class LLMProvider(
    @param:Qualifier(AIConfiguration.CHAT_LLM_BEAN_NAME)
    val chatLLM: LLM,

    @param:Qualifier(AIConfiguration.VISION_LLM_BEAN_NAME)
    val visionLLM: LLM,
)
