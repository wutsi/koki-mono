package com.wutsi.koki.portal.pub.agent.service

import com.wutsi.koki.portal.pub.agent.form.LeadForm
import com.wutsi.koki.sdk.KokiAgent
import org.springframework.stereotype.Service

@Service
class AgentService(private val koki: KokiAgent) {
    fun create(form: LeadForm) {

    }
}
